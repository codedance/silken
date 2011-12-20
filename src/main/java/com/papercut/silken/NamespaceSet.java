package com.papercut.silken;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.SoyMsgBundleHandler;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.xliffmsgplugin.XliffMsgPlugin;

/**
 * Stores fileset and cached tofu for a given namespace.
 * 
 * @author chris
 */
public class NamespaceSet {

    private static final String SOY_EXTENSION = ".soy";

    private static final String JS_SOY_EXTENSION = ".js.soy";

    private static final String MSG_EXTENSION = ".xlf";
    
    private static final String GLOBALS_EXTENSION = ".globals";

    
    private final String namespace;

    private final Config config;

    private Object cacheLock = new Object();

    private SoyTofu tofu;

    private final Map<Locale, SoyMsgBundle> msgBundleCache = new MapMaker().softValues().makeComputingMap(
            new Function<Locale, SoyMsgBundle>() {
                public SoyMsgBundle apply(Locale locale) {
                    if (locale == null) {
                        return SoyMsgBundle.EMPTY;
                    }
                    
                    final FileSetResolver fileSetResolver = config.getFileSetResolver();
                    final String searchPath = config.getSearchPath();
                    final List<URL> msgURLs = Lists.newArrayList();
                    SoyMsgBundleHandler msgBundleHandler = new SoyMsgBundleHandler(new XliffMsgPlugin());

                    
                    // Try full form first, then drop back to country only.
                    List<String> suffixes = Lists.newArrayList(
                            locale.toString() + MSG_EXTENSION, 
                            locale.getLanguage() + MSG_EXTENSION
                            );

                    for (String suffix : suffixes) {
                        // Shared namespace
                        for (String ns : config.getSharedNameSpaces()) {
                            List<URL> sharedMsgFiles = fileSetResolver.filesFromNamespace(searchPath, ns, suffix);
                            msgURLs.addAll(sharedMsgFiles);
                        }
                        // Namespace files
                        List<URL> msgFiles = fileSetResolver.filesFromNamespace(config.getSearchPath(), namespace,
                                suffix);
                        msgURLs.addAll(msgFiles);
                        
                        if (msgURLs.size() > 0) {
                            break;
                        }
                    }

                    // Convert URLs to MsgBundels
                    final List<SoyMsgBundle> msgBundles = new ArrayList<SoyMsgBundle>(msgURLs.size());
                    for (URL url : msgURLs) {
                        try {
                            msgBundles.add(msgBundleHandler.createFromResource(url));
                        } catch (Exception e) {
                            throw new RuntimeException("Unable to generate messages files from: " + url.toString(), e);
                        }
                    }

                    if (msgBundles.size() == 0) {
                        return SoyMsgBundle.EMPTY;
                    }

                    return Utils.mergeMsgBundles(msgBundles);
                }
            });

    private final Map<Locale, String> javaScriptCache = new MapMaker().softValues().makeComputingMap(
            new Function<Locale, String>() {
                public String apply(Locale locale) {
                    SoyJsSrcOptions opts = new SoyJsSrcOptions();
                    // FUTURE: Allow configuration of opts via config?
                    List<String> js = generateSoyFileSet(JS_SOY_EXTENSION).compileToJsSrc(opts,
                            msgBundleCache.get(locale));

                    StringBuilder sb = new StringBuilder();
                    for (String s : js) {
                        sb.append(s).append("\r\n");
                    }
                    return sb.toString();
                }
            });

    protected NamespaceSet(String namespace, Config config) {
        this.namespace = namespace;
        this.config = config;
        compile();
    }

    /**
     * Compile up our filesets
     */
    private void compile() {
        // Compile and store (Note: We don't generate JS as it will be rare to
        // use this here)
        tofu = generateSoyFileSet(SOY_EXTENSION).compileToJavaObj();
    }

    protected void flush() {
        synchronized (cacheLock) {
            tofu = null;
            msgBundleCache.clear();
            javaScriptCache.clear();
        }
    }

    protected String render(String templateName, SoyMapData model, Locale locale) {
        synchronized (cacheLock) {
            if (config.isDisableCaching()) {
                flush();
            }
            if (tofu == null) {
                compile();
            }
        }

        return tofu.newRenderer(templateName).setData(model).setMsgBundle(msgBundleCache.get(locale)).render();
    }

    protected String provideAsJavaScript(Locale locale) {
        synchronized (cacheLock) {
            if (config.isDisableCaching()) {
                flush();
            }
            return javaScriptCache.get(locale);
        }
    }

    private SoyFileSet generateSoyFileSet(String suffix) {
        final FileSetResolver fileSetResolver = config.getFileSetResolver();
        final String searchPath = config.getSearchPath();

        SoyFileSet.Builder builder = new SoyFileSet.Builder();

        boolean hasSetGlobals = false;
        
        // Use global compile time provider if set.
        if (config.getCompileTimeGlobalsProvider() != null) {
            builder.setCompileTimeGlobals(config.getCompileTimeGlobalsProvider().getGlobals());
            hasSetGlobals = true;
        }

        // Shared namespaces.
        for (String ns : config.getSharedNameSpaces()) {
            List<URL> sharedSoyFiles = fileSetResolver.filesFromNamespace(searchPath, ns, suffix);
            for (URL url : sharedSoyFiles) {
                builder.add(url);
            }
            
            // Any globals definition here?
            if (!hasSetGlobals) {
                List<URL> sharedGlobalFiles = fileSetResolver.filesFromNamespace(searchPath, ns, GLOBALS_EXTENSION);
                if (sharedGlobalFiles.size() > 0) {
                    try {
                        builder.setCompileTimeGlobals(sharedGlobalFiles.get(0));
                    } catch (IOException e) {
                        throw new RuntimeException("Error sourcing globals file.", e);
                    }
                    hasSetGlobals = true;
                }
            }
        }

        // Our own namespace.
        List<URL> namespaceSoyFiles = fileSetResolver.filesFromNamespace(searchPath, namespace, suffix);
        if (namespaceSoyFiles.size() == 0) {
            throw new RuntimeException("No soy template files found in namespace: " + namespace);
        }

        for (URL url : namespaceSoyFiles) {
            builder.add(url);
        }
        
        // Any compile time globals definition here?  Not recommended but we'll search.
        if (!hasSetGlobals) {
            List<URL> globalFiles = fileSetResolver.filesFromNamespace(searchPath, namespace, GLOBALS_EXTENSION);
            if (globalFiles.size() > 0) {
                try {
                    builder.setCompileTimeGlobals(globalFiles.get(0));
                } catch (IOException e) {
                    throw new RuntimeException("Error sourcing globals file.", e);
                }
                hasSetGlobals = true;
            }
        }

        // Compile and store
        return builder.build();
    }

   
   

}

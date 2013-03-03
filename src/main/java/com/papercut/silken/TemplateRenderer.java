package com.papercut.silken;

import java.util.Locale;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.template.soy.data.SoyMapData;


/**
 * 
 * There is one instances of the TemplateRender per Servlet. This code does the main rendering delegation and needs
 * to be thread-safe. This class holds a reference to a set/map of "NamespaceSets" and handles calls to render,
 * recompile, flush, flushAll.
 * 
 * @author chris
 */
public class TemplateRenderer {
    private final Config config;

    private final LoadingCache<String, NamespaceSet> namespaceSetsCache = CacheBuilder.newBuilder().softValues()
            .build(new CacheLoader<String, NamespaceSet>() {
                public NamespaceSet load(String namespace) {
                    return new NamespaceSet(namespace, config);
                }
            });

    public TemplateRenderer(Config config) {
        this.config = config;
    }

    /**
     * Renders a template using the provided data (model and injected) as SoyMapData and locale for messages. 
     * @param templateName A fully qualified template name.
     * @param model The model data.
     * @param ijData Injected data or null if no data.
     * @param locale The locale used to render messages, etc.
     * @return A string representation of the template.
     */
    public String render(String templateName, SoyMapData model, SoyMapData ijData, Locale locale) {

        int lastDot = templateName.lastIndexOf('.');
        if (lastDot == -1) {
            throw new IllegalArgumentException("No namespace and template name defined in dot format");
        }

        String namespace = templateName.substring(0, lastDot);

        return namespaceSetsCache.getUnchecked(namespace).render(templateName, model, ijData, locale);
    }
    
    /**
     * Renders a template using the provided data (model and injected) as any Silken compatible object/structure.
     */
    public String render(String templateName, Object model, Object ijData, Locale locale) {
    	return render(templateName, Utils.objectToSoyDataMap(model), Utils.objectToSoyDataMap(ijData), locale);
    }
    
    /**
     * Renders a template provided a model (as a SoyMapData).  Uses default locale.
     */
    public String render(String templateName, SoyMapData model) {
    	return render(templateName, model, null, null);
    }
    
    /**
     * Renders a template provided a model (as any Silken compatible object/structure).
     * Uses the default locale.
     */
    public String render(String templateName, Object model) {
    	return render(templateName, model, null, null);
    }

    public String provideAsJavaScript(String namespace, Locale locale) {
        return namespaceSetsCache.getUnchecked(namespace).provideAsJavaScript(locale);
    }

    public void precompile(String namespace) {
        // Fetching our namespace will fire off a compile.
        namespaceSetsCache.getUnchecked(namespace);
    }

    public void flush(String namespace) {
        NamespaceSet ns = namespaceSetsCache.getIfPresent(namespace);
        if (ns != null) {
            ns.flush();
        }
    }

    public void flushAll() {
        for (NamespaceSet ns : namespaceSetsCache.asMap().values()) {
            ns.flush();
        }
    }
}

/*
 * (c) Copyright 1999-2011 PaperCut Software International Pty. Ltd. 
 *           http://www.papercut.com/ 
 *           
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * More information on Silken: https://github.com/codedance/silken
 * 
 */
package com.papercut.silken;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.google.template.soy.data.SoyMapData;

/**
 * The main Silken servlet class. This is designed to be put on a url path like /soy.  Requests supported include:
 * 
 * /soy/com.myorg.mytemplates.myTemplate
 *      Template Forward ("render")
 * 
 * /soy/_precompile/com.myorg.mytemplates
 *      Pre-compiles all templates in the given namespace and returns 200 OK on success.
 * 
 * /soy/_flush/com.myorg.mytemplates
 *      Flushes any cached compiled templates in the given namespace forcing a recompile on next access.
 * 
 * /soy/_flushAll
 *      Flushes all cahnged compiled templates from all referenced/loaded namespaces.
 * 
 * /soy/js/[serial]/[locale]/com.myorg.mytemplates.js
 *      "provideAsJavaScript" returns a rendered JS file for all client-side templates,  where "[serial]" is an 
 *      a number/component that can be used for cache busting, [locale] is an optional component denoted the locale.  If
 *      [locale] is not defined, the locale is selected using the accept-header or as implemented by the localeResolver
 *      (see below).
 *      
 * Servlet init configuration options include:
 * 
 * showStackTracesInErrors - Set to "true" to show stack traces in the browser/response. Default: false
 * 
 * disableCaching - Set to "true" to turn off caching. Helps when authoring templates (i.e. live refresh). Default: false
 * 
 * sharedNamespaces - A comma separated list of namespaces shared (available) to all templates. Default: "shared"
 * 
 * localeResolver - Customize the locale resolver. Set to a fully qualified class name pointing to an implementation of
 *                  LocaleResolver.  Default: AcceptHeaderLocaleResolver.
 * 
 * modelResolver - Customize the model resolver. Set to a fully qualified class name pointing to an implementation of
 *                 ModelResolver.  Default: RequestAttributeModelResolver.
 * 
 * fileSetResolver - Customize the model resolver. Set to a fully qualified class name pointing to an implementation of
 *                 FileSetResolver. Default: WebAppFileSetResolver.
 * 
 * compileTimeGlobalsProvider - Provide a custom map of Soy Template compile time globals. Default: none
 * 
 * runtimeGlobalsProvider - Provide a custom map of runtime globals passed into every template render. Default: none
 * 
 * precompileNamespaces - a comma separated list of namespaces to precompile.
 * 
 * searchPath - Advanced: Modify the default search path used to locate *.soy and associated files. Value is a semicolon
 * 				separated path that may contain/reference $CLASSPATH and $WEBROOT.
 * 				Default:  $CLASSPATH:$WEBROOT/templates:$WEBROOT/WEB-INF/templates
 * 
 * @author chris
 */
public class SilkenServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String HTML_CONTENT_TYPE = "text/html";

    private static final String JS_CONTENT_TYPE = "text/javascript";
    
    private static final String UTF8_ENCODING = "UTF-8";
    

    private final Config config = new Config();

    private final TemplateRenderer templateRenderer = new TemplateRenderer(config);

    public Config getConfig() {
        return config;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        String disableCaching = servletConfig.getInitParameter("disableCaching");
        if (disableCaching != null) {
            config.setDisableCaching(isValueTrue(disableCaching));
        }
        if (System.getProperty("silken.disableCaching") != null) {
        	config.setDisableCaching(true);
        }
        
        String sharedNamespaces = servletConfig.getInitParameter("sharedNamespaces");
        if (!Strings.isNullOrEmpty(sharedNamespaces)) {
        	config.setSharedNameSpaces(Arrays.asList(sharedNamespaces.split("[,;]")));
        }

        String localeResolver = servletConfig.getInitParameter("localeResolver");
        if (localeResolver != null) {
            try {
                Object resolver = Class.forName(localeResolver).newInstance();
                config.setLocaleResolver((LocaleResolver) resolver);
            } catch (Exception e) {
                throw new ServletException("Unable to create localeResolver", e);
            }
        }

        String modelResolver = servletConfig.getInitParameter("modelResolver");
        if (modelResolver != null) {
            try {
                Object resolver = Class.forName(modelResolver).newInstance();
                config.setModelResolver((ModelResolver) resolver);
            } catch (Exception e) {
                throw new ServletException("Unable to create modelResolver", e);
            }
        }

        String fileSetResolver = servletConfig.getInitParameter("fileSetResolver");
        if (fileSetResolver != null) {
            try {
                Object resolver = Class.forName(fileSetResolver).newInstance();
                config.setFileSetResolver((FileSetResolver) resolver);
            } catch (Exception e) {
                throw new ServletException("Unable to create fileSetResolver", e);
            }
        } else {
            // Instance our default - passing in reference to our servlet context.
            config.setFileSetResolver(new WebAppFileSetResolver(getServletContext()));
        }

        String compileTimeGlobalsProvider = servletConfig.getInitParameter("compileTimeGlobalsProvider");
        if (compileTimeGlobalsProvider != null) {
            try {
                Object provider = Class.forName(compileTimeGlobalsProvider).newInstance();
                config.setCompileTimeGlobalsProvider((CompileTimeGlobalsProvider) provider);
            } catch (Exception e) {
                throw new ServletException("Unable to create compileTimeGlobalsProvider", e);
            }
        }
        
        String runtimeGlobalsProvider = servletConfig.getInitParameter("runtimeGlobalsProvider");
        if (runtimeGlobalsProvider != null) {
            try {
                Object provider = Class.forName(runtimeGlobalsProvider).newInstance();
                config.setRuntimeGlobalsProvider((RuntimeGlobalsProvider) provider);
            } catch (Exception e) {
                throw new ServletException("Unable to create runtimeGlobalsProvider", e);
            }
        }

        String stackTraces = servletConfig.getInitParameter("showStackTracesInErrors");
        if (stackTraces != null) {
            if (isValueFalse(stackTraces)) {
                config.setShowStackTracesInErrors(false);
            } else {
                config.setShowStackTracesInErrors(true);
            }
        }
        
        String searchPath = servletConfig.getInitParameter("searchPath");
        if (searchPath != null) {
        	config.setSearchPath(searchPath);
        }

        // Store a reference config in our context so external code can modify.
        getServletContext().setAttribute("silken.config", config);
        
        // Store a reference to our template render so external code can access for raw rendering is required.
        getServletContext().setAttribute("silken.templateRenderer", templateRenderer);
        
        // Do we have some namespaces defined to precompile?
        String namespaces = servletConfig.getInitParameter("precompileNamespaces");
        if (!Strings.isNullOrEmpty(namespaces)) {
        	for (String ns : namespaces.split("[,;]")) {
        		try {
        			templateRenderer.precompile(ns);
        		} catch (Exception e) {
        			// Do our best. Ignore.
        			servletConfig.getServletContext().log("Unable to precompile namespace: " + ns, e);
        		}	
        	}
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doRequest(req, resp);
    }

    private void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            final String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.isEmpty() || pathInfo.equals("/")) {
                error(req, resp, new RuntimeException("No valid soy template defined. Check the path."));
                return;
            }

            // Path is pathInfo minus leading slash to make it easier to work with.
            final String path = pathInfo.substring(1);

            // Any paths starting with an underscore may/will be a special management command.
            if (path.startsWith("_")) {
                
                if (path.startsWith("_precompile/")) {
                    templateRenderer.precompile(namespaceFromPath(path));
                    return;
                }

                if (path.startsWith("_flush/")) {
                    templateRenderer.flush(namespaceFromPath(path));
                    return;
                }
                
                if (path.startsWith("_flushAll")) {
                    templateRenderer.flushAll();
                    return;
                }
                
            }
                
            // If starts with js/ then we're requested templates as JavaScript 
            // Format : js/[serial]/[optional:locale]/namespace[.js]
            if (path.startsWith("js/")) {
                Locale locale = null;
                
                String namespace = namespaceFromPath(path);
                if (namespace.endsWith(".js")) {
                    namespace = namespace.substring(0, namespace.length() - 3);
                }
                
                String[] components = path.split("/");
                if (components.length == 3) {
                    locale = config.getLocaleResolver().resolveLocale(req);
                } else if (components.length == 4) {
                    locale = Utils.stringToLocale(components[2]);
                } else {
                    throw new RuntimeException(
                            "Request not in the format: /soy/js/[serial]/[optional:locale]/namespace.js" 
                            );
                }

                resp.setContentType(JS_CONTENT_TYPE);
                resp.setCharacterEncoding(UTF8_ENCODING);
                resp.setHeader("Cache-Control", "max-age=" + Long.toString(config.getJavaScriptCacheMaxAge()));
                resp.getWriter().print(templateRenderer.provideAsJavaScript(namespace, locale));
                return;
            }


            final Locale locale = config.getLocaleResolver().resolveLocale(req);
            final String templateName = path;
            
            SoyMapData model = config.getModelResolver().resolveModel(req);
            SoyMapData globals = null;
            if (config.getRuntimeGlobalsProvider() != null) {
                globals = config.getRuntimeGlobalsProvider().getGlobals(req);
            }
            
            // FUTURE: A mime type resolver and character type encoding?
            resp.setContentType(HTML_CONTENT_TYPE);
            resp.setCharacterEncoding(UTF8_ENCODING);
            resp.getWriter().print(templateRenderer.render(templateName, model, globals, locale));
            return;

        } catch (Exception e) {
            error(req, resp, e);
        }

    }

    private void error(HttpServletRequest req, HttpServletResponse resp, Exception ex) throws ServletException,
            IOException {
        StringBuffer html = new StringBuffer();
        html.append("<html>");
        html.append("<title>Error</title>");
        html.append("<body bgcolor=\"#ffffff\">");
        html.append("<h2>SoyTemplateRenderer: Error rendering template</h2>");
        html.append("<pre style='white-space: pre-wrap;'>");
        String why = ex.getMessage();
        if (why != null && why.trim().length() > 0) {
            html.append(why);
            html.append("<br>");
        }

        if (config.isShowStackTracesInErrors()) {
            html.append("<br>");
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            html.append(sw.toString());
        }

        html.append("</pre>");
        html.append("</body>");
        html.append("</html>");
        resp.getWriter().append(html.toString());
    }
    
    private String namespaceFromPath(String path) {
        final int slashPos = path.lastIndexOf('/');
        return path.substring(slashPos + 1);
    }
    
    private boolean isValueTrue(String value) {
        value = Strings.nullToEmpty(value).trim().toLowerCase();
        // true, 1 or yes.
        return (value.startsWith("t") || value.equals("1") || value.startsWith("y"));
    }
    
    private boolean isValueFalse(String value) {
        value = Strings.nullToEmpty(value).trim().toLowerCase();
        // false, 0 or no
        return (value.startsWith("f") || value.equals("0") || value.startsWith("n"));
    }
    
}

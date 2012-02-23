package com.papercut.silken;

import java.util.Locale;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
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

    private final Map<String, NamespaceSet> namespaceSetsCache = new MapMaker().softValues().makeComputingMap(
            new Function<String, NamespaceSet>() {
                public NamespaceSet apply(String namespace) {
                    return new NamespaceSet(namespace, config);
                }
            });

    public TemplateRenderer(Config config) {
        this.config = config;
    }

    public String render(String templateName, SoyMapData model, SoyMapData ijData, Locale locale) {

        int lastDot = templateName.lastIndexOf('.');
        if (lastDot == -1) {
            throw new IllegalArgumentException("No namespace and template name defined in dot format");
        }

        String namespace = templateName.substring(0, lastDot);

        return namespaceSetsCache.get(namespace).render(templateName, model, ijData, locale);
    }

    public String provideAsJavaScript(String namespace, Locale locale) {
        return namespaceSetsCache.get(namespace).provideAsJavaScript(locale);
    }

    public void precompile(String namespace) {
        // Fetching our namespace will fire off a compile.
        namespaceSetsCache.get(namespace);
    }

    public void flush(String namespace) {
        if (namespaceSetsCache.containsKey(namespace)) {
            namespaceSetsCache.get(namespace).flush();
        }
    }

    public void flushAll() {
        for (NamespaceSet ns : namespaceSetsCache.values()) {
            ns.flush();
        }
    }
}

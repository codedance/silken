package com.papercut.silken;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import com.google.common.base.Preconditions;

/**
 * File Set Resolver for a Servlet Web App Container environment. It supports
 * searching for resources in either the Classpath or the web app's web root.
 * 
 * @author chris
 */
public class WebAppFileSetResolver implements FileSetResolver {

    private static final String CLASSPATH_VAR = "$CLASSPATH";

    private static final String WEB_APP_ROOT_VAR = "$WEBROOT";

    private ServletContext servletContext;

    public WebAppFileSetResolver(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public List<URL> filesFromNamespace(String searchPath, String namespace, String suffix) {

        Preconditions.checkNotNull(searchPath, "A searchpath is required");
        Preconditions.checkNotNull(namespace, "A namespace is required");
        Preconditions.checkNotNull(suffix, "A file name suffix is required");

        List<URL> resourcesList = new ArrayList<URL>();

        for (String path : searchPath.split("[;:,]")) {
            path = path.trim();
            if (CLASSPATH_VAR.equals(path)) {
                resourcesList.addAll(listResourcesFromClasspath(namespace, suffix));
            }

            if (path.startsWith(WEB_APP_ROOT_VAR)) {
                // Format: $WEBDIR/base/
                String base = "/";
                if (!WEB_APP_ROOT_VAR.equals(path)) {
                    base = path.substring(WEB_APP_ROOT_VAR.length());
                }
                resourcesList.addAll(listResourcesFromWebDir(base, namespace, suffix));
            }

            // We and return when we find our first match on out path.
            if (resourcesList.size() > 0) {
                return resourcesList;
            }
        }
        return resourcesList;
    }

    private List<URL> listResourcesFromClasspath(String namespace, String suffix) {
        Enumeration<URL> resources = null;
        List<URL> resourceList = new ArrayList<URL>();
        try {
            resources = getClass().getClassLoader().getResources(namespace);
        } catch (IOException e) {
            throw new RuntimeException("Unable to resolve namespace: " + namespace, e);
        }

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getPath().endsWith(suffix)) {
                resourceList.add(resource);
            }
        }

        return resourceList;
    }

    private List<URL> listResourcesFromWebDir(String base, String namespace, String suffix) {
        Preconditions.checkNotNull(base, "No base defined");

        // Make sure we end in a slash
        StringBuilder path = new StringBuilder(base);
        if (!base.endsWith("/")) {
            path.append('/');
        }
        path.append(namespace.replace(".", "/")).append('/');

        Set<String> files = (Set<String>) servletContext.getResourcePaths(path.toString());

        List<URL> resourceList = new ArrayList<URL>();
        for (String file : files) {
            if (file.endsWith(suffix)) {
                try {
                    resourceList.add(servletContext.getResource(file));
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Unable to resolve resource at: " + file, e);
                }
            }
        }
        return resourceList;
    }

}

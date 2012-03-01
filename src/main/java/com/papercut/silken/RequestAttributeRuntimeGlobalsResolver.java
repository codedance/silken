package com.papercut.silken;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.template.soy.data.SoyMapData;

/**
 * This implementation extracts the globals ($ij injected data) from a HTTP request attribute. If you'd like to change the 
 * default attribute key, simply subclass this implementation and set via the super constructor, 
 * and reference the class via the "runtimeGlobalsResolver" Servlet init parameter.
 * 
 * @author chris
 */
public class RequestAttributeRuntimeGlobalsResolver implements RuntimeGlobalsResolver {
	
	private static final String DEFAULT_GLOBALS_KEY = "globals";

    private final String globalsKey;

    public RequestAttributeRuntimeGlobalsResolver() {
        this.globalsKey = DEFAULT_GLOBALS_KEY;
    }

    public RequestAttributeRuntimeGlobalsResolver(String globalsKey) {
        this.globalsKey = globalsKey;
    }

    /* (non-Javadoc)
     * @see com.papercut.silken.RuntimeGlobalsResolver#resolveGlobals(javax.servlet.http.HttpServletRequest)
     */
    public SoyMapData resolveGlobals(HttpServletRequest request) {
        Object globals = request.getAttribute(globalsKey);
        if (globals == null) {
            return null;
        }

        if (globals instanceof SoyMapData) {
            return (SoyMapData) globals;
        }

        Map<String, ?> globalsMap  = Utils.toSoyCompatibleMap(globals);
       
        return new SoyMapData(globalsMap);
    }


}

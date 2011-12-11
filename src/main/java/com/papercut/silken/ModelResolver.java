package com.papercut.silken;

import javax.servlet.http.HttpServletRequest;

import com.google.template.soy.data.SoyMapData;

/**
 * Extract the model (SoyMapData) which will be used to render the template.  If implemented this will be called
 * once per request.
 * 
 * @author chris
 */
public interface ModelResolver {
    
    /**
     * @param request The HttpServletRequest associated with the render resource.
     * @return The model data.
     */
    SoyMapData resolveModel(HttpServletRequest request);

}

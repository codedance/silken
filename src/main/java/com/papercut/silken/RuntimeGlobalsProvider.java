package com.papercut.silken;

import javax.servlet.http.HttpServletRequest;

import com.google.template.soy.data.SoyMapData;

/**
 * Implement to add a common set of globals to the model passed to the closure templates.
 * 
 * @author chris
 */
public interface RuntimeGlobalsProvider {
    
    /**
     * @param request The HttpServletRequest associated with the render request.
     * @return Return a map of key/values.
     */
    SoyMapData getGlobals(HttpServletRequest request) ;

}

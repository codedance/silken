package com.papercut.silken;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * Used to resolve the locale of a request.
 * 
 * @author chris
 */
public interface LocaleResolver {

    /**
     * Called once per request
     * @param request The HttpServletRequest associated with the render request.
     * @return The Locale which will be used to render the template.
     */
    Locale resolveLocale(HttpServletRequest request);

}

package com.papercut.silken;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * Default implementation of the LocaleResolver that uses the accept-language header.
 * 
 * @author chris
 */
public class AcceptHeaderLocaleResolver implements LocaleResolver {

    /**
     * @see com.papercut.silken.LocaleResolver#resolveLocale(javax.servlet.http.HttpServletRequest)
     */
    public Locale resolveLocale(HttpServletRequest request) {
        return request.getLocale();
    }

}

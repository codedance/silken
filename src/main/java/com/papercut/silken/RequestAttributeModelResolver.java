package com.papercut.silken;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.template.soy.data.SoyMapData;

/**
 * This implementation extracts the model from a HTTP request attribute. If you'd like to change the default model key,
 * simply subclass this implementation and set via the super constructor, and reference the class via the
 * "modelResolver" Servlet init parameter.
 * 
 * @author chris
 */
public class RequestAttributeModelResolver implements ModelResolver {

    private static final String DEFAULT_MODEL_KEY = "model";

    private final String modelKey;

    public RequestAttributeModelResolver() {
        this.modelKey = DEFAULT_MODEL_KEY;
    }

    public RequestAttributeModelResolver(String modelKey) {
        this.modelKey = modelKey;
    }

    /**
     * @see com.papercut.silken.ModelResolver#resolveModel(javax.servlet.http.HttpServletRequest)
     */
    @SuppressWarnings("unchecked")
    public SoyMapData resolveModel(HttpServletRequest request) {
        Object model = request.getAttribute(modelKey);
        if (model == null) {
            return null;
        }

        if (model instanceof SoyMapData) {
            return (SoyMapData) model;
        }

        Map<String, ?> modelMap;
        if (model instanceof Map) {
            modelMap = (Map<String, ?>) model;
        } else {
            // FUTURE: Supported nested pojo's.
            modelMap = Utils.pojoToMap(model);
        }

        return new SoyMapData(modelMap);
    }

}

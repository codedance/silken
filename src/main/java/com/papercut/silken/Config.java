package com.papercut.silken;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * This config class holds various global configuration. Multiple layers have a reference to this configuration
 * store/POJO.
 * 
 * @author chris
 */
public class Config {

    private List<String> sharedNameSpaces;

    private boolean disableCaching;

    private CompileTimeGlobalsProvider compileTimeGlobalsProvider;
    
    private RuntimeGlobalsProvider runtimeGlobalsProvider;

    private LocaleResolver localeResolver;

    private ModelResolver modelResolver;

    private FileSetResolver fileSetResolver;
    
    private long javaScriptCacheMaxAge = 2592000L; // 30-days in seconds

    private String searchPath;

    private long lastChange;

    private boolean showStackTracesInErrors;

    public Config() {
        sharedNameSpaces = Lists.newArrayList("shared");
        disableCaching = false;
        compileTimeGlobalsProvider = null;
        runtimeGlobalsProvider = null;
        localeResolver = new AcceptHeaderLocaleResolver();
        modelResolver = new RequestAttributeModelResolver();
        searchPath = "$CLASSPATH:$WEBROOT/templates:$WEBROOT/templates";
        showStackTracesInErrors = true;
    }

    public List<String> getSharedNameSpaces() {
        return sharedNameSpaces;
    }

    public void setSharedNameSpaces(List<String> sharedNameSpaces) {
        this.sharedNameSpaces = sharedNameSpaces;
    }

    public boolean isDisableCaching() {
        return disableCaching;
    }

    public void setDisableCaching(boolean disableCaching) {
        this.disableCaching = disableCaching;
    }

    public CompileTimeGlobalsProvider getCompileTimeGlobalsProvider() {
        return compileTimeGlobalsProvider;
    }

    public void setCompileTimeGlobalsProvider(CompileTimeGlobalsProvider compileTimeGlobalsProvider) {
        this.compileTimeGlobalsProvider = compileTimeGlobalsProvider;
    }

    public RuntimeGlobalsProvider getRuntimeGlobalsProvider() {
		return runtimeGlobalsProvider;
	}

	public void setRuntimeGlobalsProvider(RuntimeGlobalsProvider runtimeGlobalsProvider) {
		this.runtimeGlobalsProvider = runtimeGlobalsProvider;
	}

	public LocaleResolver getLocaleResolver() {
        return localeResolver;
    }

    public void setLocaleResolver(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    public ModelResolver getModelResolver() {
        return modelResolver;
    }

    public void setModelResolver(ModelResolver modelResolver) {
        this.modelResolver = modelResolver;
    }

    public long getJavaScriptCacheMaxAge() {
        return javaScriptCacheMaxAge;
    }

    public void setJavaScriptCacheMaxAge(long javaScriptCacheMaxAge) {
        this.javaScriptCacheMaxAge = javaScriptCacheMaxAge;
    }

    public FileSetResolver getFileSetResolver() {
        return fileSetResolver;
    }

    public void setFileSetResolver(FileSetResolver fileSetResolver) {
        this.fileSetResolver = fileSetResolver;
    }

    public String getSearchPath() {
        return searchPath;
    }

    public void setSearchPath(String searchPath) {
        this.searchPath = searchPath;
    }

    public long getLastChange() {
        return lastChange;
    }

    public void setLastChange(long lastChange) {
        this.lastChange = lastChange;
    }

    public boolean isShowStackTracesInErrors() {
        return showStackTracesInErrors;
    }

    public void setShowStackTracesInErrors(boolean showStackTracesInErrors) {
        this.showStackTracesInErrors = showStackTracesInErrors;
    }

}

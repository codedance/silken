package com.papercut.silken;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * This config class holds various global configuration. Multiple layers have a reference to this configuration
 * store/POJO.
 * 
 * Developers: Take care to ensure data changes are visible across threds (thread-safe).
 * 
 * @author chris
 */
public class Config {

    private List<String> sharedNameSpaces;

    private boolean disableCaching;

    private CompileTimeGlobalsProvider compileTimeGlobalsProvider;
    
    private RuntimeGlobalsResolver runtimeGlobalsResolver;

    private LocaleResolver localeResolver;

    private ModelResolver modelResolver;

    private FileSetResolver fileSetResolver;
    
    private long javaScriptCacheMaxAge = 2592000L; // 30-days in seconds

    private String searchPath;

    private long lastChange;

    private boolean showStackTracesInErrors;

    public Config() {
        sharedNameSpaces = ImmutableList.of("shared");
        disableCaching = false;
        compileTimeGlobalsProvider = null;
        runtimeGlobalsResolver = new RequestAttributeRuntimeGlobalsResolver();
        localeResolver = new AcceptHeaderLocaleResolver();
        modelResolver = new RequestAttributeModelResolver();
        searchPath = "$CLASSPATH:$WEBROOT/templates:$WEBROOT/WEB-INF/templates";
        showStackTracesInErrors = true;
    }

    public synchronized List<String> getSharedNameSpaces() {
        return sharedNameSpaces;
    }

    public synchronized void setSharedNameSpaces(List<String> sharedNameSpaces) {
        // To ensure we're threadsafe we'll copy to an immutable list
        this.sharedNameSpaces = ImmutableList.copyOf(sharedNameSpaces);
    }

    public synchronized boolean isDisableCaching() {
        return disableCaching;
    }

    public synchronized void setDisableCaching(boolean disableCaching) {
        this.disableCaching = disableCaching;
    }

    public synchronized CompileTimeGlobalsProvider getCompileTimeGlobalsProvider() {
        return compileTimeGlobalsProvider;
    }

    public synchronized void setCompileTimeGlobalsProvider(CompileTimeGlobalsProvider compileTimeGlobalsProvider) {
        this.compileTimeGlobalsProvider = compileTimeGlobalsProvider;
    }

	public synchronized RuntimeGlobalsResolver getRuntimeGlobalsResolver() {
		return runtimeGlobalsResolver;
	}

	public synchronized void setRuntimeGlobalsResolver(RuntimeGlobalsResolver runtimeGlobalsResolver) {
		this.runtimeGlobalsResolver = runtimeGlobalsResolver;
	}

	public synchronized LocaleResolver getLocaleResolver() {
        return localeResolver;
    }

    public synchronized void setLocaleResolver(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    public synchronized ModelResolver getModelResolver() {
        return modelResolver;
    }

    public synchronized void setModelResolver(ModelResolver modelResolver) {
        this.modelResolver = modelResolver;
    }

    public synchronized long getJavaScriptCacheMaxAge() {
        return javaScriptCacheMaxAge;
    }

    public synchronized void setJavaScriptCacheMaxAge(long javaScriptCacheMaxAge) {
        this.javaScriptCacheMaxAge = javaScriptCacheMaxAge;
    }

    public synchronized FileSetResolver getFileSetResolver() {
        return fileSetResolver;
    }

    public synchronized void setFileSetResolver(FileSetResolver fileSetResolver) {
        this.fileSetResolver = fileSetResolver;
    }

    public synchronized String getSearchPath() {
        return searchPath;
    }

    public synchronized void setSearchPath(String searchPath) {
        this.searchPath = searchPath;
    }

    public synchronized long getLastChange() {
        return lastChange;
    }

    public synchronized void setLastChange(long lastChange) {
        this.lastChange = lastChange;
    }

    public synchronized boolean isShowStackTracesInErrors() {
        return showStackTracesInErrors;
    }

    public synchronized void setShowStackTracesInErrors(boolean showStackTracesInErrors) {
        this.showStackTracesInErrors = showStackTracesInErrors;
    }

}

package com.papercut.silken;

import java.net.URL;
import java.util.List;

/**
 * Interface for a file set resolve. It takes a search path (say from configuration), a namespace and a file suffix and
 * returns a list of matching files (URLs).
 * 
 * @author chris
 */
public interface FileSetResolver {

    List<URL> filesFromNamespace(String searchPath, String namespace, String suffix);

}

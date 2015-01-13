package com.papercut.silken;

import com.google.inject.Module;

import java.util.Map;

/**
 * Implement this interface to provide custom modules for soy generation.
 *
 * @author arlo
 */
public interface ModuleProvider {

    /**
     * @return Return an iterable of modules
     */
    Iterable<Module> getModules();

}

package com.balugaq.runtimepylon.exceptions;

import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PluginDesc;

import java.util.List;

public class PluginDependencyMissingException extends RuntimeException {
    public PluginDependencyMissingException() {
        super();
    }

    public PluginDependencyMissingException(Pack pack, List<PluginDesc> pluginDesc) {
        super("Unable to load " + pack.getPackID() + " as missing Plugin dependency " + pluginDesc);
    }
}

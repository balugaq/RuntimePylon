package com.balugaq.pc.exceptions;

import com.balugaq.pc.config.Pack;
import com.balugaq.pc.config.PluginDesc;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public class PluginDependencyMissingException extends RuntimeException {
    public PluginDependencyMissingException() {
        super();
    }

    public PluginDependencyMissingException(Pack pack, List<PluginDesc> pluginDesc) {
        super("Unable to load " + pack.getPackID() + " as missing Plugin dependency " + pluginDesc);
    }
}

package com.balugaq.pc.integration;

import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public interface Integration {
    Plugin plugin();
    void apply();
    default void shutdown() {
    }
}

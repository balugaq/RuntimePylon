package com.balugaq.runtimepylon.config.preloads;

import com.balugaq.runtimepylon.config.PostLoadable;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public record PreparedBlock(
        RegisteredObjectID id,
        Material material,
        boolean postLoad
) implements PostLoadable {
}

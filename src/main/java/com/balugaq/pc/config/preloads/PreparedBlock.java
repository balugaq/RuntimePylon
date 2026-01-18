package com.balugaq.pc.config.preloads;

import com.balugaq.pc.config.PostLoadable;
import com.balugaq.pc.config.RegisteredObjectID;
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

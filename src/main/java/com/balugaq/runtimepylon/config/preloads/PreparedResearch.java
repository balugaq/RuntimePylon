package com.balugaq.runtimepylon.config.preloads;

import com.balugaq.runtimepylon.config.PostLoadable;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public record PreparedResearch(
        RegisteredObjectID id,
        Material material,
        @Nullable String name,
        long cost,
        List<String> unlocks,
        boolean postLoad
) implements PostLoadable {
}

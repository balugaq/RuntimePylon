package com.balugaq.runtimepylon.config.preloads;

import com.balugaq.runtimepylon.config.PostLoadable;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public record PreparedBlock(
        RegisteredObjectID id,
        Material material,
        @Nullable ScriptDesc script,
        boolean postLoad
) implements PostLoadable {
}

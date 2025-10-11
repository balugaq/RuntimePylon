package com.balugaq.runtimepylon.config;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record PreparedBlock(
        RegisteredObjectID id,
        Material material,
        @Nullable ScriptDesc script,
        boolean postLoad
) implements PostLoadable {
}

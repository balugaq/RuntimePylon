package com.balugaq.runtimepylon.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@Data
@AllArgsConstructor
@NullMarked
public class PreparedBlock implements PostLoadable {
    final RegisteredObjectID id;
    final Material material;
    final @Nullable ScriptDesc script;
    final boolean postLoad;
}

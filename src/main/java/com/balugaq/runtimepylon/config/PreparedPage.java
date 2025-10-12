package com.balugaq.runtimepylon.config;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record PreparedPage(RegisteredObjectID id, Material material, @Nullable UnsArrayList<PageDesc> parents, boolean postLoad) implements PostLoadable {
}

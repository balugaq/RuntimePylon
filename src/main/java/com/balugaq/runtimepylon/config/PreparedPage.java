package com.balugaq.runtimepylon.config;

import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record PreparedPage(RegisteredObjectID id, Material material, boolean postLoad) implements PostLoadable {
}

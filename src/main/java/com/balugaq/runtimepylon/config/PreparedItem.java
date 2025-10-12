package com.balugaq.runtimepylon.config;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record PreparedItem(
        RegisteredObjectID id,
        ItemStack icon,
        @Nullable ScriptDesc script,
        @Nullable List<PageDesc> pages,
        boolean postLoad
) implements PostLoadable {
}
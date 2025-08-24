package com.balugaq.runtimepylon.block.base;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WithPage extends WithModel {
    @Nullable NamespacedKey getPageId();

    @NotNull WithPage setPageId(@NotNull NamespacedKey key);
}

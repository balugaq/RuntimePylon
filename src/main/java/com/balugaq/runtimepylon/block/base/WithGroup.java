package com.balugaq.runtimepylon.block.base;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WithGroup extends WithModel {
    @Nullable NamespacedKey getGroupId();
    @NotNull WithGroup setGroupId(@NotNull NamespacedKey key);
}

package com.balugaq.runtimepylon.block.base;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WithModel {
    @Nullable ItemStack getModel();
    @Nullable NamespacedKey getItemId();
    @NotNull WithModel setModel(@Nullable ItemStack model);
    @NotNull WithModel setItemId(@Nullable NamespacedKey itemId);
}

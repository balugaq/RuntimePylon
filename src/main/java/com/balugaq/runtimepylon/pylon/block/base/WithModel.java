package com.balugaq.runtimepylon.pylon.block.base;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WithModel {
    @Nullable ItemStack getModel();

    @NotNull WithModel setModel(@Nullable ItemStack model);

    @Nullable NamespacedKey getItemId();

    @NotNull WithModel setItemId(@Nullable NamespacedKey itemId);
}

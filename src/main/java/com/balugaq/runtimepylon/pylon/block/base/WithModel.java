package com.balugaq.runtimepylon.pylon.block.base;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public interface WithModel {
    @Nullable ItemStack getModel();

    WithModel setModel(@Nullable ItemStack model);

    @Nullable NamespacedKey getItemId();

    WithModel setItemId(@Nullable NamespacedKey itemId);
}

package com.balugaq.runtimepylon.pylon.block.base;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
@Deprecated
public interface WithModel {
    @Nullable ItemStack getModel();

    void setModel(@Nullable ItemStack model);

    @Nullable NamespacedKey getItemId();

    void setItemId(@Nullable NamespacedKey itemId);
}

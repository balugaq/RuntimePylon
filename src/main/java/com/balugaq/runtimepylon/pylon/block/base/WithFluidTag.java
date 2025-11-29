package com.balugaq.runtimepylon.pylon.block.base;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public interface WithFluidTag {
    @Nullable ItemStack getTag();

    WithFluidTag setTag(@Nullable ItemStack tag);
}

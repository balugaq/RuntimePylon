package com.balugaq.runtimepylon.pylon.block.base;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WithFluidTag {
    @Nullable ItemStack getTag();

    @NotNull WithFluidTag setTag(@Nullable ItemStack tag);
}

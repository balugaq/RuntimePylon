package com.balugaq.runtimepylon.pylon.block.base;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

/**
 * @author balugaq
 */
@NullMarked
public interface WithRecipe {
    Map<Integer, ItemStack> getRecipe();

    void setRecipe(Map<Integer, ItemStack> recipe);
}

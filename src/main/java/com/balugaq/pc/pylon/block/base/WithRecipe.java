package com.balugaq.pc.pylon.block.base;

import org.bukkit.inventory.ItemStack;
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

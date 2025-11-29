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
public interface WithRecipe extends WithModel {
    @Nullable NamespacedKey getRecipeTypeId();

    WithRecipe setRecipeTypeId(@Nullable NamespacedKey recipeTypeId);

    Map<Integer, ItemStack> getRecipe();

    WithRecipe setRecipe(Map<Integer, ItemStack> recipe);
}

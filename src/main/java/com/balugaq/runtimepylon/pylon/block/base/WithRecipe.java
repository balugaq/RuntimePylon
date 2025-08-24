package com.balugaq.runtimepylon.pylon.block.base;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface WithRecipe extends WithModel {
    @Nullable NamespacedKey getRecipeTypeId();

    @NotNull WithRecipe setRecipeTypeId(@Nullable NamespacedKey recipeTypeId);

    @NotNull Map<Integer, ItemStack> getRecipe();

    @NotNull WithRecipe setRecipe(@NotNull Map<Integer, ItemStack> recipe);
}

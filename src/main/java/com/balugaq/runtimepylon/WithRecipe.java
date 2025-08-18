package com.balugaq.runtimepylon;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface WithRecipe {
    @Nullable NamespacedKey getRecipeTypeId();
    @NotNull Map<Integer, ItemStack> getRecipe();
}

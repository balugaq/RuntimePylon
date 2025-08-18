package com.balugaq.runtimepylon;

import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public record RecipeAdapter<T extends PylonRecipe>(
        RecipeType<T> recipeType,
        BiFunction<NamespacedKey, Map<Integer, ItemStack>, T> mapper
) {
    public static final Map<RecipeType<? extends PylonRecipe>, RecipeAdapter<? extends PylonRecipe>> adapters = new HashMap<>();

    public RecipeAdapter(@NotNull RecipeType<T> recipeType, @NotNull BiFunction<@NotNull NamespacedKey, @NotNull Map<Integer, ItemStack>, @Nullable T> mapper) {
        this.recipeType = recipeType;
        this.mapper = mapper;
        adapters.put(recipeType, this);
    }

    public boolean apply(@NotNull NamespacedKey key, @NotNull Map<Integer, ItemStack> recipe) {
        T instance = mapper.apply(key, recipe);
        if (instance == null) return false;
        recipeType.addRecipe(instance);
        return true;
    }

    public void removeRecipe(@NotNull NamespacedKey key) {
        recipeType.removeRecipe(key);
    }

    @Nullable
    public static <T extends PylonRecipe> RecipeAdapter<T> find(RecipeType<T> recipeType, Class<? extends PylonRecipe> pylonRecipeClass) {
        return (RecipeAdapter<T>) adapters.get(recipeType);
    }
}

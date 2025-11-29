package com.balugaq.runtimepylon.util;

import com.balugaq.runtimepylon.exceptions.WrongStateException;
import com.balugaq.runtimepylon.pylon.item.DataStack;
import com.balugaq.runtimepylon.pylon.item.NumberStack;
import com.balugaq.runtimepylon.pylon.item.StringStack;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.recipe.vanilla.BlastingRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.CampfireRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.FurnaceRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.ShapedRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.ShapelessRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.SmithingTransformRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.SmokingRecipeWrapper;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

import static com.balugaq.runtimepylon.util.Lang.*;

/**
 * @author balugaq
 */
@Deprecated
@NullMarked
public record RecipeAdapter<T extends PylonRecipe>(
        RecipeType<T> recipeType,
        RecipeResolver<T> mapper
) {
    @Getter
    private static final Map<RecipeType<? extends PylonRecipe>, RecipeAdapter<? extends PylonRecipe>> adapters = new HashMap<>();

    static {
        // @formatter:off
        register(RecipeType.VANILLA_BLASTING, (key, model,recipe) ->
            new BlastingRecipeWrapper(new BlastingRecipe(
                    key,
                    model,
                    toChoice(find(recipe, 1)),
                    findFloat(recipe, 1),
                    findInt(recipe, 1)
            ))
        );
        register(RecipeType.VANILLA_CAMPFIRE, (key, model,recipe) ->
            new CampfireRecipeWrapper(new CampfireRecipe(
                    key,
                    model,
                    toChoice(find(recipe, 1)),
                    findFloat(recipe, 1),
                    findInt(recipe, 1)
            ))
        );
        register(RecipeType.VANILLA_FURNACE, (key, model,recipe) ->
            new FurnaceRecipeWrapper(new FurnaceRecipe(
                    key,
                    model,
                    toChoice(find(recipe, 1)),
                    findFloat(recipe, 1),
                    findInt(recipe, 1)
            ))
        );
        register(RecipeType.VANILLA_SMOKING, (key, model,recipe) ->
            new SmokingRecipeWrapper(new SmokingRecipe(
                    key,
                    model,
                    toChoice(find(recipe, 1)),
                    findFloat(recipe, 1),
                    findInt(recipe, 1)
            ))
        );
        register(RecipeType.VANILLA_SMITHING_TRANSFORM, (key, model,recipe) ->
            new SmithingTransformRecipeWrapper(new SmithingTransformRecipe(
                    key,
                    model,                        // result
                    toChoice(find(recipe, 1)), // template
                    toChoice(find(recipe, 2)), // base
                    toChoice(find(recipe, 3))  // addition
            ))
        );
        register(RecipeType.VANILLA_SHAPED, (key, model,recipe) -> {
            ShapedRecipe r = new ShapedRecipe(key, model)
                    .shape(
                            "123",
                            "456",
                            "789"
                    );
            recipe.forEach((i, itemStack) -> {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    r.setIngredient(String.valueOf(i).charAt(0), itemStack);
                }
            });
            return new ShapedRecipeWrapper(r);
        });
        register(RecipeType.VANILLA_SHAPELESS, (key, model,recipe) -> {
            ShapelessRecipe r = new ShapelessRecipe(key, model);
            int cnt = 0;
            for (ItemStack itemStack : recipe.values()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    cnt += itemStack.getAmount();
                    if (cnt > 9) {
                        throw new WrongStateException(recipe_err_1);
                    }
                    r.addIngredient(itemStack.clone());
                }
            }
            return new ShapelessRecipeWrapper(r);
        });
        // @formatter:on
    }

    public RecipeAdapter(RecipeType<T> recipeType, RecipeResolver<T> mapper) {
        this.recipeType = recipeType;
        this.mapper = mapper;
        adapters.put(recipeType, this);
    }

    public static ItemStack find(Map<Integer, ItemStack> recipe, int n) {
        int cnt = 0;
        for (ItemStack itemStack : recipe.values()) {
            if (itemStack != null && itemStack.getType() != Material.AIR && ++cnt == n) return itemStack;
        }
        throw new WrongStateException("#" + cnt + recipe_err_2);
    }

    public static int findInt(Map<Integer, ItemStack> recipe, int n) {
        int cnt = 0;
        for (ItemStack itemStack : recipe.values()) {
            if (PylonItem.fromStack(itemStack) instanceof NumberStack intStack && ++cnt == n) return intStack.toInt();
        }
        throw new WrongStateException("#" + cnt + recipe_err_3);
    }

    public static float findFloat(Map<Integer, ItemStack> recipe, int n) {
        int cnt = 0;
        for (ItemStack itemStack : recipe.values()) {
            if (PylonItem.fromStack(itemStack) instanceof NumberStack intStack && ++cnt == n) return intStack.toFloat();
        }
        throw new WrongStateException("#" + cnt + recipe_err_4);
    }

    public static double findDouble(Map<Integer, ItemStack> recipe, int n) {
        int cnt = 0;
        for (ItemStack itemStack : recipe.values()) {
            if (PylonItem.fromStack(itemStack) instanceof NumberStack intStack && ++cnt == n)
                return intStack.toDouble();
        }
        throw new WrongStateException("#" + cnt + recipe_err_5);
    }

    public static boolean findBoolean(Map<Integer, ItemStack> recipe, int n) {
        int cnt = 0;
        for (ItemStack itemStack : recipe.values()) {
            PylonItem pylon = PylonItem.fromStack(itemStack);
            if ((pylon instanceof DataStack)) {
                if (pylon instanceof StringStack string) {
                    if (++cnt == n) return string.toBoolean();
                } else if (pylon instanceof NumberStack number) {
                    if (++cnt == n) return number.toBoolean();
                }
            }
        }
        throw new WrongStateException("#" + cnt + recipe_err_6);
    }

    public static String findString(Map<Integer, ItemStack> recipe, int n) {
        int cnt = 0;
        for (ItemStack itemStack : recipe.values()) {
            if (PylonItem.fromStack(itemStack) instanceof StringStack string && ++cnt == n) return string.get();
        }
        throw new WrongStateException("#" + cnt + recipe_err_7);
    }

    public static @Nullable ItemStack findNullable(Map<Integer, ItemStack> recipe, int n) {
        int cnt = 0;
        for (ItemStack itemStack : recipe.values()) {
            if (++cnt == n) return itemStack;
        }
        return null;
    }

    public static RecipeChoice.ExactChoice toChoice(ItemStack itemStack) {
        return new RecipeChoice.ExactChoice(itemStack);
    }

    public static <T extends PylonRecipe> RecipeAdapter<? extends PylonRecipe> register(RecipeType<T> recipeType, RecipeResolver<T> mapper) {
        return adapters.put(recipeType, new RecipeAdapter<>(recipeType, mapper));
    }

    @Nullable
    public static <T extends PylonRecipe> RecipeAdapter<T> find(RecipeType<T> recipeType, Class<? extends PylonRecipe> pylonRecipeClass) {
        return (RecipeAdapter<T>) adapters.get(recipeType);
    }

    public boolean apply(NamespacedKey key, ItemStack model, Map<Integer, ItemStack> recipe) {
        T instance = mapper.apply(key, model, recipe);
        if (instance == null) return false;
        recipeType.addRecipe(instance);
        return true;
    }

    public boolean noRecipe(NamespacedKey key, ItemStack model, Map<Integer, ItemStack> recipe) {
        return Bukkit.getRecipe(key) == null;
    }

    public void removeRecipe(NamespacedKey key) {
        recipeType.removeRecipe(key);
    }

    public interface RecipeResolver<T extends PylonRecipe> {
        @Nullable
        T apply(NamespacedKey key, ItemStack model, Map<Integer, ItemStack> recipe);
    }
}

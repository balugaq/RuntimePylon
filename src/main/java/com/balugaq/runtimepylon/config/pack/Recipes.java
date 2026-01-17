package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.StackFormatter;
import com.balugaq.runtimepylon.exceptions.InvalidNamespacedKeyException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import com.balugaq.runtimepylon.util.StringUtil;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.recipe.ConfigurableRecipeType;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.recipe.vanilla.BlastingRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.CampfireRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.FurnaceRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.ShapedRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.ShapelessRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.SmithingTransformRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.SmithingTrimRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.SmokingRecipeWrapper;
import io.github.pylonmc.pylon.core.recipe.vanilla.TransmuteRecipeWrapper;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import kotlin.jvm.functions.Function5;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.TransmuteRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author balugaq
 */
@Data
@RequiredArgsConstructor
@NullMarked
public class Recipes {
    private AtomicInteger loadedRecipes = new AtomicInteger(0);
    private final File recipeFolder;
    private final PackNamespace namespace;
    private final Map<RecipeType<?>, Set<NamespacedKey>> registeredRecipes = new HashMap<>();

    public void loadRecipes() {
        for (var dir : recipeFolder.listFiles()) {
            if (!dir.isDirectory()) continue;
            var namespace = dir.getName();
            for (var cfg : dir.listFiles()) {
                if (!cfg.getName().endsWith(".yml")) continue;
                try (var sk2 = StackFormatter.setPosition("Loading file: " + StringUtil.simplifyPath(cfg.getAbsolutePath()))) {

                var key = new NamespacedKey(namespace, cfg.getName().substring(0, cfg.getName().length() - 4));
                var type = PylonRegistry.RECIPE_TYPES.get(key);
                if (ADVANCED_RECIPE_TYPES.containsKey(key)) {
                    File old = new File(new File(Pack.getRecipesFolder(), namespace), cfg.getName());
                    if (old.exists()) old.delete();
                }
                if (!(type instanceof ConfigurableRecipeType<?> ctp)) {
                    continue;
                }

                Consumer<NamespacedKey> success = k -> {
                    loadedRecipes.incrementAndGet();
                    if (!registeredRecipes.containsKey(ctp)) {
                        registeredRecipes.put(ctp, new HashSet<>());
                    }
                    registeredRecipes.get(ctp).add(k);
                };

                if (ADVANCED_RECIPE_TYPES.containsKey(key)) {
                    var cg = YamlConfiguration.loadConfiguration(new File(new File(recipeFolder, namespace), cfg.getName()));
                    loadRecipesAdvanced(this.namespace, key, ctp, cg, success);
                } else {
                    var config = new Config(cfg.toPath());
                    loadRecipesNormal(this.namespace, ctp, config, success);
                }

                } catch (Exception e) {
                    StackFormatter.handle(e);
                }
            }
        }
    }

    public static void loadRecipes(PackNamespace namespace, ConfigurableRecipeType<?> ctp, Consumer<NamespacedKey> success, Set<String> keys, BiConsumer<NamespacedKey, String> loader) {
        for (var ky : keys) {
            var k = NamespacedKey.fromString(ky);
            if (k == null) {
                if (ky.contains(":")) {
                    // a custom key
                    throw new InvalidNamespacedKeyException(ky);
                }
                // default namespace
                k = new NamespacedKey(namespace.getNamespace(), ky);
            }
            try (var sk = StackFormatter.setPosition("Reading " + ky)) {
                loader.accept(k, ky);
                success.accept(k);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Failed to load recipe with key " + ky + " from config for recipe type " + ctp.getKey(),
                        e
                );
            }
        }
    }

    public static void loadRecipesNormal(PackNamespace namespace, ConfigurableRecipeType<?> ctp, ConfigSection config, Consumer<NamespacedKey> success) {
        loadRecipes(namespace, ctp, success, config.getKeys(), (k, ky) -> {
            try {

            ReflectionUtil.invokeMethod(
                    ctp,
                    "addRecipe",
                    ReflectionUtil.invokeMethod(
                            ctp,
                            "loadRecipe",
                            k,
                            config.getSectionOrThrow(ky)
                    )
            );

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void loadRecipesAdvanced(PackNamespace namespace, NamespacedKey key, ConfigurableRecipeType<?> ctp, ConfigurationSection cg, Consumer<NamespacedKey> success) {
        loadRecipes(namespace, ctp, success, cg.getKeys(false), (k, ky) -> {
            try {

            ReflectionUtil.invokeMethod(
                    ctp,
                    "addRecipe",
                    ADVANCED_RECIPE_TYPES.get(key).apply(k, cg.getConfigurationSection(ky))
            );

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static final Map<NamespacedKey, BiFunction<NamespacedKey, ConfigurationSection, ? extends PylonRecipe>> ADVANCED_RECIPE_TYPES = new HashMap<>();

    private static final int DEFAULT_COOKING_TIME = 100;

    static {
        loadAdvance(RecipeType.VANILLA_BLASTING, Recipes::advancedVanillaBlasting);
        loadAdvance(RecipeType.VANILLA_CAMPFIRE, Recipes::advancedVanillaCampfire);
        loadAdvance(RecipeType.VANILLA_FURNACE, Recipes::advancedVanillaFurnace);
        loadAdvance(RecipeType.VANILLA_SHAPED, Recipes::advancedVanillaShaped);
        loadAdvance(RecipeType.VANILLA_SHAPELESS, Recipes::advancedVanillaShapeless);
        loadAdvance(RecipeType.VANILLA_TRANSMUTE, Recipes::advancedVanillaTransmute);
        loadAdvance(RecipeType.VANILLA_SMITHING_TRANSFORM, Recipes::advancedVanillaSmithingTransform);
        loadAdvance(RecipeType.VANILLA_SMITHING_TRIM, Recipes::advancedVanillaSmithingTrim);
        loadAdvance(RecipeType.VANILLA_SMOKING, Recipes::advancedVanillaSmoking);
    }

    private static SmithingTrimRecipeWrapper advancedVanillaSmithingTrim(NamespacedKey key, ConfigurationSection config) {
        var pattern = Deserializer.TRIM_PATTERN.deserialize(config.get("pattern"));
        var template = Deserializer.RECIPE_CHOICE.deserialize(config.get("template"));
        var base = Deserializer.RECIPE_CHOICE.deserialize(config.get("base"));
        var addition = Deserializer.RECIPE_CHOICE.deserialize(config.get("addition"));
        return new SmithingTrimRecipeWrapper(
                new SmithingTrimRecipe(
                        key,
                        template,
                        base,
                        addition,
                        pattern
                )
        );
    }

    private static SmithingTransformRecipeWrapper advancedVanillaSmithingTransform(NamespacedKey key, ConfigurationSection config) {
        var template = Deserializer.RECIPE_CHOICE.deserialize(config.get("template"));
        var base = Deserializer.RECIPE_CHOICE.deserialize(config.get("base"));
        var addition = Deserializer.RECIPE_CHOICE.deserialize(config.get("addition"));
        var result = Deserializer.ITEMSTACK.deserialize(config.get("result"));
        return new SmithingTransformRecipeWrapper(
                new SmithingTransformRecipe(
                        key,
                        result,
                        template,
                        base,
                        addition
                )
        );
    }

    private static TransmuteRecipeWrapper advancedVanillaTransmute(NamespacedKey key, ConfigurationSection config) {
        var result = Deserializer.MATERIAL.deserialize(config.get("result"));
        var recipe = new TransmuteRecipe(key, result, Deserializer.RECIPE_CHOICE.deserialize(config.get("input")), Deserializer.RECIPE_CHOICE.deserialize(config.get("material")));
        var category = Deserializer.CRAFTING_BOOK_CATEGORY.deserializeOrNull(config.get("category"));
        var group = config.getString("group");
        recipe.setCategory(category == null ? CraftingBookCategory.MISC : category);
        recipe.setGroup(group == null ? "" : group);
        return new TransmuteRecipeWrapper(recipe);
    }

    private static ShapelessRecipeWrapper advancedVanillaShapeless(NamespacedKey key, ConfigurationSection config) {
        List<?> is = config.getList("ingredients");
        List<RecipeChoice.ExactChoice> ingredients = (is == null || is.isEmpty()) ? new ArrayList<>() : is.stream().map(Deserializer.RECIPE_CHOICE::deserialize).toList();
        var result = Deserializer.ITEMSTACK.deserialize(config.get("result"));

        var recipe = new ShapelessRecipe(key, result);
        for (var ingredient : ingredients) {
            recipe.addIngredient(ingredient);
        }
        var category = Deserializer.CRAFTING_BOOK_CATEGORY.deserializeOrNull(config.get("category"));
        if (category != null) recipe.setCategory(category);
        var group = config.getString("group");
        if (group != null) recipe.setGroup(group);
        return new ShapelessRecipeWrapper(recipe);
    }

    private static ShapedRecipeWrapper advancedVanillaShaped(NamespacedKey key, ConfigurationSection config) {
        Map<Character, RecipeChoice.ExactChoice> ingredientKey = new HashMap<>();
        var c = config.getConfigurationSection("key");
        if (c == null) throw new MissingArgumentException("key");
        for (var e : c.getKeys(false)) {
            if (e.isEmpty()) continue;;
            ingredientKey.put(e.toCharArray()[0], Deserializer.RECIPE_CHOICE.deserialize(c.get(e)));
        }
        var pattern = config.getStringList("pattern");
        var result = Deserializer.ITEMSTACK.deserialize(config.get("result"));

        var recipe = new ShapedRecipe(key, result);
        recipe.shape(pattern.toArray(new String[0]));
        for (var e : ingredientKey.entrySet()) {
            recipe.setIngredient(e.getKey(), e.getValue());
        }
        var category = Deserializer.CRAFTING_BOOK_CATEGORY.deserializeOrNull(config.get("category"));
        if (category != null) recipe.setCategory(category);
        var group = config.getString("group");
        if (group != null) recipe.setGroup(group);
        return new ShapedRecipeWrapper(recipe);
    }

    private static <T extends CookingRecipe<T>> T advancedVanillaCooking(NamespacedKey key, ConfigurationSection config, Function5<NamespacedKey, ItemStack, RecipeChoice, Float, Integer, T> function) {
        var cookingTime = config.getInt("cookingtime", DEFAULT_COOKING_TIME);
        var experience = (float) config.getDouble("experience", 0f);
        var ingredient = Deserializer.RECIPE_CHOICE.deserialize(config.get("ingredient"));
        var result = Deserializer.ITEMSTACK.deserialize(config.get("result"));
        var recipe = function.invoke(key, result, ingredient, experience, cookingTime);
        var category = Deserializer.COOKING_BOOK_CATEGORY.deserializeOrNull(config.get("category"));
        if (category != null) recipe.setCategory(category);
        var group = config.getString("group");
        if (group != null) recipe.setGroup(group);
        return recipe;
    }

    private static BlastingRecipeWrapper advancedVanillaBlasting(NamespacedKey key, ConfigurationSection config) {
        return new BlastingRecipeWrapper(advancedVanillaCooking(key, config, BlastingRecipe::new));
    }

    private static CampfireRecipeWrapper advancedVanillaCampfire(NamespacedKey key, ConfigurationSection config) {
        return new CampfireRecipeWrapper(advancedVanillaCooking(key, config, CampfireRecipe::new));
    }

    private static FurnaceRecipeWrapper advancedVanillaFurnace(NamespacedKey key, ConfigurationSection config) {
        return new FurnaceRecipeWrapper(advancedVanillaCooking(key, config, FurnaceRecipe::new));
    }

    private static SmokingRecipeWrapper advancedVanillaSmoking(NamespacedKey key, ConfigurationSection config) {
        return new SmokingRecipeWrapper(advancedVanillaCooking(key, config, SmokingRecipe::new));
    }

    public static <T extends PylonRecipe> void loadAdvance(RecipeType<T> type, BiFunction<NamespacedKey, ConfigurationSection, T> function) {
        if (type instanceof ConfigurableRecipeType<T>) ADVANCED_RECIPE_TYPES.put(type.getKey(), function);
    }
}

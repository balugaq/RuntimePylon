package com.balugaq.pc.integration;

import com.balugaq.pc.config.Deserializer;
import com.balugaq.pc.config.Pack;
import com.balugaq.pc.config.pack.Recipes;
import com.balugaq.pc.data.MyArrayList;
import com.balugaq.pc.data.WeightedElement;
import com.balugaq.pc.exceptions.MissingArgumentException;
import io.github.pylonmc.pylon.base.recipes.BloomeryDisplayRecipe;
import io.github.pylonmc.pylon.base.recipes.DrillingDisplayRecipe;
import io.github.pylonmc.pylon.base.recipes.ForgingDisplayRecipe;
import io.github.pylonmc.pylon.base.recipes.GrindstoneRecipe;
import io.github.pylonmc.pylon.base.recipes.HammerRecipe;
import io.github.pylonmc.pylon.base.recipes.MagicAltarRecipe;
import io.github.pylonmc.pylon.base.recipes.MeltingRecipe;
import io.github.pylonmc.pylon.base.recipes.MixingPotRecipe;
import io.github.pylonmc.pylon.base.recipes.MoldingRecipe;
import io.github.pylonmc.pylon.base.recipes.PipeBendingRecipe;
import io.github.pylonmc.pylon.base.recipes.PitKilnRecipe;
import io.github.pylonmc.pylon.base.recipes.PressRecipe;
import io.github.pylonmc.pylon.base.recipes.SmelteryRecipe;
import io.github.pylonmc.pylon.base.recipes.TableSawRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.github.pylonmc.pylon.core.util.WeightedSet;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author balugaq
 */
@NullMarked
public class PylonBaseIntegration implements Integration {
    public static final Deserializer<MiningLevel> MINING_LEVEL = Deserializer.enumDeserializer(MiningLevel.class).forceUpperCase();
    private final Plugin plugin;
    public PylonBaseIntegration() {
        this.plugin = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("PylonBase"));
    }

    @Override
    public Plugin plugin() {
        return plugin;
    }

    public static BloomeryDisplayRecipe advancedBloomeryDisplay(NamespacedKey key, ConfigurationSection section) {
        return new BloomeryDisplayRecipe(
                key,
                Deserializer.ITEMSTACK.deserialize(section.get("input")),
                Deserializer.ITEMSTACK.deserialize(section.get("result"))
        );
    }

    public static DrillingDisplayRecipe advancedDrillingDisplay(NamespacedKey key, ConfigurationSection section) {
        return new DrillingDisplayRecipe(
                key,
                Deserializer.ITEMSTACK.deserialize(section.get("input")),
                Deserializer.ITEMSTACK.deserialize(section.get("result"))
        );
    }

    public static ForgingDisplayRecipe advancedForgingDisplay(NamespacedKey key, ConfigurationSection section) {
        return new ForgingDisplayRecipe(
                key,
                Deserializer.ITEMSTACK.deserialize(section.get("input")),
                Deserializer.ITEMSTACK.deserialize(section.get("result"))
        );
    }

    public static GrindstoneRecipe advancedGrindstone(NamespacedKey key, ConfigurationSection section) {
        return new GrindstoneRecipe(
                key,
                Deserializer.RECIPE_INPUT_ITEM.deserialize(section.get("input")),
                toWeightedSet(Pack.read(section, MyArrayList.class, WeightedElement.class, "results")),
                section.getInt("cycles"),
                Deserializer.BLOCK_DATA.deserialize(section.get("particle-data"))
        );
    }

    public static WeightedSet<ItemStack> toWeightedSet(List<WeightedElement> list) {
        WeightedSet<ItemStack> set = new WeightedSet<>();
        for (WeightedElement element : list) {
            set.add(new WeightedSet.Element<>(element.getElement(), element.getWeight()));
        }
        return set;
    }

    public static HammerRecipe advancedHammer(NamespacedKey key, ConfigurationSection section) {
        return new HammerRecipe(
                key,
                Deserializer.RECIPE_INPUT_ITEM.deserialize(section.get("input")),
                Deserializer.ITEMSTACK.deserialize(section.get("result")),
                MINING_LEVEL.deserialize(section.get("mining-level")),
                (float) section.getDouble("chance")
        );
    }

    public static MagicAltarRecipe advancedMagicAltar(NamespacedKey key, ConfigurationSection section) {
        var se = section.getConfigurationSection("key");
        if (se == null) throw new MissingArgumentException("key");
        Map<Character, RecipeInput.Item> itemMap = new HashMap<>();
        for (String ke : se.getKeys(false)) {
            itemMap.put(ke.charAt(0), Deserializer.RECIPE_INPUT_ITEM.deserialize(se.get(ke)));
        }
        var shape = section.getStringList("shape");
        StringBuilder ingredientChars = new StringBuilder();
        ingredientChars.append(shape.getFirst());
        ingredientChars.append(shape.get(1).charAt(2));
        ingredientChars.append(new StringBuilder(shape.get(2)).reverse());
        ingredientChars.append(shape.get(1).charAt(0));
        List<RecipeInput.@Nullable Item> inputs = new ArrayList<>(8);
        for (int i = 0; i < ingredientChars.length(); i++) {
            char c = ingredientChars.charAt(i);
            if (c == ' ') {
                inputs.add(null);
            } else if (itemMap.containsKey(c)) {
                inputs.add(itemMap.get(c));
            } else {
                throw new IllegalArgumentException("Unknown character in shape: " + c);
            }
        }
        RecipeInput.Item catalyst = itemMap.get(shape.get(1).charAt(1));
        if (catalyst == null) {
            throw new IllegalArgumentException("Catalyst (center item) cannot be empty");
        }

        return new MagicAltarRecipe(
                key,
                inputs,
                catalyst,
                Deserializer.ITEMSTACK.deserialize(section.get("result")),
                section.getInt("time-seconds")
        );
    }

    public static MeltingRecipe advancedMelting(NamespacedKey key, ConfigurationSection section) {
        return new MeltingRecipe(
                key,
                Deserializer.RECIPE_INPUT_ITEM.deserialize(section.get("input")),
                Deserializer.PYLON_FLUID.deserialize(section.get("result")),
                section.getDouble("amount"),
                section.getDouble("temperature")
        );
    }

    public static MixingPotRecipe advancedMixingPot(NamespacedKey key, ConfigurationSection section) {
        return new MixingPotRecipe(
                key,
                Pack.read(section, MyArrayList.class, Deserializer.RECIPE_INPUT_ITEM, "input-items"),
                Deserializer.RECIPE_INPUT_FLUID.deserialize(section.get("input-fluid")),
                Deserializer.FLUID_OR_ITEM.deserialize(section.get("output")),
                section.getBoolean("requires-enriched-fire", false)
        );
    }

    public static MoldingRecipe advancedMolding(NamespacedKey key, ConfigurationSection section) {
        return new MoldingRecipe(
                key,
                Deserializer.ITEMSTACK.deserialize(section.get("input")),
                Deserializer.ITEMSTACK.deserialize(section.get("result")),
                section.getInt("clicks")
        );
    }

    public static PipeBendingRecipe advancedPipeBending(NamespacedKey key, ConfigurationSection section) {
        return new PipeBendingRecipe(
                key,
                Deserializer.RECIPE_INPUT_ITEM.deserialize(section.get("input")),
                Deserializer.ITEMSTACK.deserialize(section.get("result")),
                Deserializer.BLOCK_DATA.deserialize(section.get("particle-data")),
                section.getInt("time-ticks")
        );
    }

    public static PitKilnRecipe advancedPitKiln(NamespacedKey key, ConfigurationSection section) {
        return new PitKilnRecipe(
                key,
                Pack.read(section, MyArrayList.class, Deserializer.RECIPE_INPUT_ITEM, "inputs"),
                Pack.read(section, MyArrayList.class, Deserializer.ITEMSTACK, "outputs")
        );
    }

    public static PressRecipe advancedPress(NamespacedKey key, ConfigurationSection section) {
        return new PressRecipe(
                key,
                Deserializer.RECIPE_INPUT_ITEM.deserialize(section.get("input")),
                section.getDouble("oil-amount")
        );
    }

    public static SmelteryRecipe advancedSmeltery(NamespacedKey key, ConfigurationSection section) {
        return new SmelteryRecipe(
                key,
                Deserializer.FLUID_MAP.deserialize(section.get("inputs")),
                Deserializer.FLUID_MAP.deserialize(section.get("outputs")),
                section.getDouble("temperature")
        );
    }

    public static TableSawRecipe advancedTableSaw(NamespacedKey key, ConfigurationSection section) {
        return new TableSawRecipe(
                key,
                Deserializer.ITEMSTACK.deserialize(section.get("input")),
                Deserializer.ITEMSTACK.deserialize(section.get("result")),
                Deserializer.BLOCK_DATA.deserialize(section.get("particle-data")),
                section.getInt("time-ticks")
        );
    }

    @Override
    public void apply() {
        Recipes.loadAdvance(BloomeryDisplayRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedBloomeryDisplay);
        Recipes.loadAdvance(DrillingDisplayRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedDrillingDisplay);
        Recipes.loadAdvance(ForgingDisplayRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedForgingDisplay);
        Recipes.loadAdvance(GrindstoneRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedGrindstone);
        Recipes.loadAdvance(HammerRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedHammer);
        Recipes.loadAdvance(MagicAltarRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedMagicAltar);
        Recipes.loadAdvance(MeltingRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedMelting);
        Recipes.loadAdvance(MixingPotRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedMixingPot);
        Recipes.loadAdvance(MoldingRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedMolding);
        Recipes.loadAdvance(PipeBendingRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedPipeBending);
        Recipes.loadAdvance(PitKilnRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedPitKiln);
        Recipes.loadAdvance(PressRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedPress);
        Recipes.loadAdvance(SmelteryRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedSmeltery);
        Recipes.loadAdvance(TableSawRecipe.RECIPE_TYPE, PylonBaseIntegration::advancedTableSaw);
    }
}

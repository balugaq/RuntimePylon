package com.balugaq.runtimepylon.object;

import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;
import java.util.Map;

/**
 * @author balugaq
 */
@Data
@RequiredArgsConstructor
@NullMarked
public class CustomRecipe implements PylonRecipe {
    private final CustomRecipeType recipeType;
    private final NamespacedKey key;
    private final List<RecipeInput> inputs;
    private final List<FluidOrItem> results;
    private final int timeTicks;
    private final Map<String, Object> other;

    @Override
    public List<RecipeInput> getInputs() {
        return inputs;
    }

    @Override
    public List<FluidOrItem> getResults() {
        return results;
    }

    @Override
    public Gui display() {
        return recipeType.makeGui(Gui.normal(), this);
    }

    /**
     * Return the namespaced identifier for this object.
     *
     * @return this object's key
     */
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Getter
    @Setter
    @Nullable
    private Object2IntLinkedOpenHashMap<ItemStack> countOutputItems;

    @Getter
    @Setter
    @Nullable
    private Object2DoubleOpenHashMap<PylonFluid> countOutputFluids;
}

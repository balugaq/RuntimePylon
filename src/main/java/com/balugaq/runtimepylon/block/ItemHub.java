package com.balugaq.runtimepylon.block;

import com.balugaq.runtimepylon.RuntimeKeys;
import com.balugaq.runtimepylon.block.base.WithGroup;
import com.balugaq.runtimepylon.block.base.WithModel;
import com.balugaq.runtimepylon.block.base.WithRecipe;
import com.balugaq.runtimepylon.gui.ButtonSet;
import com.balugaq.runtimepylon.util.Key;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import kotlin.Pair;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class ItemHub extends PylonBlock implements
        PylonGuiBlock,
        WithModel,
        WithGroup,
        WithRecipe
{
    public boolean placeable = false;
    public @Nullable ItemStack model = null;
    public @Nullable NamespacedKey itemId = null;
    public @Nullable NamespacedKey groupId = null;
    public @Nullable NamespacedKey recipeTypeId = null;
    public @NotNull Map<Integer, ItemStack> recipe = new HashMap<>();

    public ItemHub(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public ItemHub(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        placeable = pdc.get(RuntimeKeys.placeable, PylonSerializers.BOOLEAN);
        model = pdc.get(RuntimeKeys.model, PylonSerializers.ITEM_STACK);
        itemId = pdc.get(RuntimeKeys.itemId, PylonSerializers.NAMESPACED_KEY);
        groupId = pdc.get(RuntimeKeys.groupId, PylonSerializers.NAMESPACED_KEY);
        recipeTypeId = pdc.get(RuntimeKeys.recipeTypeId, PylonSerializers.NAMESPACED_KEY);
        recipe = fromArray(pdc, "recipe");
    }

    public static @NotNull Map<Integer, ItemStack> fromArray(@NotNull PersistentDataContainer pdc, @NotNull String key) {
        return pdc.getKeys().stream().filter(k -> k.toString().startsWith(key))
                .map(k -> new Pair<>(k, pdc.get(k, PylonSerializers.ITEM_STACK)))
                .collect(Collectors.toMap(
                        p -> Integer.parseInt(p.getFirst().toString().substring(key.length())),
                        Pair::getSecond
                ));
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        super.write(pdc);
        pdc.set(RuntimeKeys.placeable, PylonSerializers.BOOLEAN, placeable);
        if (model != null) pdc.set(RuntimeKeys.model, PylonSerializers.ITEM_STACK, model);
        if (itemId != null) pdc.set(RuntimeKeys.itemId, PylonSerializers.NAMESPACED_KEY, itemId);
        if (groupId != null) pdc.set(RuntimeKeys.groupId, PylonSerializers.NAMESPACED_KEY, groupId);
        if (recipeTypeId != null) pdc.set(RuntimeKeys.recipeTypeId, PylonSerializers.NAMESPACED_KEY, recipeTypeId);
        recipe.forEach((key, value) -> pdc.set(Key.create("recipe" + key), PylonSerializers.ITEM_STACK, value));
    }

    @Override
    public @NotNull Gui createGui() {
        ButtonSet<?> buttons = new ButtonSet<>(this);
        return Gui.normal()
                .setStructure(
                        "x x x x x x x x x",
                        ". k . e E 1 2 3 .",
                        ". i . g t 4 5 6 .",
                        "p s . d D 7 8 9 .",
                        "x x x x x x x x x"
                )
                .addIngredient('x', buttons.blackBackground)
                .addIngredient('.', buttons.grayBackground)
                .addIngredient('e', buttons.setItemGroup)
                .addIngredient('E', buttons.setRecipe)
                .addIngredient('d', buttons.unsetItemGroup)
                .addIngredient('D', buttons.unsetRecipe)
                .addIngredient('k', buttons.setId)
                .addIngredient('g', buttons.itemGroup)
                .addIngredient('t', buttons.recipeType)
                .addIngredient('i', buttons.item)
                .addIngredient('1', buttons.recipe(1))
                .addIngredient('2', buttons.recipe(2))
                .addIngredient('3', buttons.recipe(3))
                .addIngredient('4', buttons.recipe(4))
                .addIngredient('5', buttons.recipe(5))
                .addIngredient('6', buttons.recipe(6))
                .addIngredient('7', buttons.recipe(7))
                .addIngredient('8', buttons.recipe(8))
                .addIngredient('9', buttons.recipe(9))
                .addIngredient('s', buttons.registerItem)
                .addIngredient('p', buttons.placeable)
                .build();
    }

    @Override
    public @NotNull WithGroup setGroupId(@NotNull NamespacedKey key) {
        this.groupId = key;
        return this;
    }

    @Override
    public @NotNull WithRecipe setRecipeTypeId(@Nullable NamespacedKey recipeTypeId) {
        this.recipeTypeId = recipeTypeId;
        return this;
    }

    @Override
    public @NotNull WithRecipe setRecipe(@NotNull Map<Integer, ItemStack> recipe) {
        this.recipe = recipe;
        return this;
    }

    @Override
    public @NotNull WithModel setModel(@Nullable ItemStack model) {
        this.model = model;
        return this;
    }

    @Override
    public @NotNull WithModel setItemId(@Nullable NamespacedKey itemId) {
        this.itemId = itemId;
        return this;
    }

    @Override
    public @NotNull WithModel setPlaceable(boolean placeable) {
        this.placeable = placeable;
        return this;
    }
}

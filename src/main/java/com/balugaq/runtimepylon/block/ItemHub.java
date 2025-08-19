package com.balugaq.runtimepylon.block;

import com.balugaq.runtimepylon.block.base.WithGroup;
import com.balugaq.runtimepylon.block.base.WithModel;
import com.balugaq.runtimepylon.block.base.WithRecipe;
import com.balugaq.runtimepylon.gui.ButtonSet;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
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

@Getter
public class ItemHub extends PylonBlock implements
        PylonGuiBlock,
        WithModel,
        WithGroup,
        WithRecipe
{
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
    }

    @Override
    public @NotNull Gui createGui() {
        ButtonSet buttons = new ButtonSet(this);
        return Gui.normal()
                .setStructure(
                        "x x x x x x x x x",
                        ". k . e E 1 2 3 .",
                        ". i . g t 4 5 6 .",
                        ". . . d D 7 8 9 .",
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
}

package com.balugaq.runtimepylon;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;

import java.util.HashMap;
import java.util.Map;

@Setter
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

    public ItemHub(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull Gui createGui() {
        ButtonSet buttons = new ButtonSet(this);
        return Gui.normal()
                .setStructure(
                        "x x x x x x x x x",
                        ". k . e E 0 1 2 .",
                        ". i . g t 3 4 5 .",
                        ". . . d D 6 7 8 .",
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
                .addIngredient('0', buttons.recipe(0))
                .addIngredient('1', buttons.recipe(1))
                .addIngredient('2', buttons.recipe(2))
                .addIngredient('3', buttons.recipe(3))
                .addIngredient('4', buttons.recipe(4))
                .addIngredient('5', buttons.recipe(5))
                .addIngredient('6', buttons.recipe(6))
                .addIngredient('7', buttons.recipe(7))
                .addIngredient('8', buttons.recipe(8))
                .build();
    }
}

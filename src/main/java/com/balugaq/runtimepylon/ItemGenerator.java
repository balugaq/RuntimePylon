package com.balugaq.runtimepylon;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.Map;

public class ItemGenerator extends PylonBlock implements PylonGuiBlock {
    public ItemStack model;
    public NamespacedKey itemId;
    public NamespacedKey groupId;
    public NamespacedKey recipeTypeId;
    public Map<Integer, ItemStack> recipe;

    public ItemGenerator(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull Gui createGui() {
        Buttons buttons = new Buttons(this);
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
                .build();
    }
}

package com.balugaq.pc.pylon.block;

import com.balugaq.pc.gui.ButtonSet;
import com.balugaq.pc.pylon.PylonCustomizerKeys;
import com.balugaq.pc.util.Keys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import kotlin.Pair;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author balugaq
 */
@Getter
@Setter
@NullMarked
public class RecipeCopier extends PylonBlock implements PylonGuiBlock {
    public Map<Integer, ItemStack> recipe = new HashMap<>();

    public RecipeCopier(Block block, BlockCreateContext context) {
        super(block, context);
    }

    public RecipeCopier(Block block, PersistentDataContainer pdc) {
        super(block, pdc);
        recipe = fromArray(pdc, "recipe");
    }

    public static Map<Integer, ItemStack> fromArray(PersistentDataContainer pdc, String key) {
        return pdc.getKeys().stream().filter(k -> k.toString().startsWith(key))
                .map(k -> new Pair<>(k, pdc.get(k, PylonSerializers.ITEM_STACK)))
                .collect(Collectors.toMap(
                        p -> Integer.parseInt(p.getFirst().toString().substring(key.length())),
                        Pair::getSecond
                ));
    }

    @Override
    public void write(PersistentDataContainer pdc) {
        super.write(pdc);
        recipe.forEach((key, value) -> pdc.set(Keys.create("recipe" + key), PylonSerializers.ITEM_STACK, value));
    }

    @Override
    public Gui createGui() {
        RecipeCopierButtonSet<?> buttons = new RecipeCopierButtonSet<>(this);
        return Gui.normal()
                .setStructure(
                        "x . I I I I I . x",
                        "x . I 1 2 3 I . x",
                        "x . I 4 5 6 I . x",
                        "x . I 7 8 9 I . x",
                        "x . O o p q O . x",
                        "x . O O r O O . x"
                )
                .addIngredient('x', buttons.blackBackground)
                .addIngredient('I', buttons.inputBorder)
                .addIngredient('O', buttons.outputBorder)
                .addIngredient('1', buttons.recipe(1))
                .addIngredient('2', buttons.recipe(2))
                .addIngredient('3', buttons.recipe(3))
                .addIngredient('4', buttons.recipe(4))
                .addIngredient('5', buttons.recipe(5))
                .addIngredient('6', buttons.recipe(6))
                .addIngredient('7', buttons.recipe(7))
                .addIngredient('8', buttons.recipe(8))
                .addIngredient('9', buttons.recipe(9))
                .addIngredient('o', buttons.recipe(10))
                .addIngredient('p', buttons.recipe(11))
                .addIngredient('q', buttons.recipe(12))
                .addIngredient('r', buttons.makeRecipe)
                .build();
    }

    @Override
    public Map<String, Inventory> createInventoryMapping() {
        return Map.of();
    }

    @Getter
    public static class RecipeCopierButtonSet<T extends RecipeCopier> extends ButtonSet<T> {
        public final AbstractItem
                makeRecipe;

        public RecipeCopierButtonSet(T b2) {
            super(b2);

            makeRecipe = create()
                    .item(data -> ItemStackBuilder.pylon(
                            Material.CRAFTING_TABLE,
                            PylonCustomizerKeys.make_recipe
                    ))
                    .click((data, clickType, player, event) -> {
                        var recipe = data.getRecipe();
                        if (recipe.isEmpty()) {
                            return false;
                        }
                        List<ItemStack> inputs = new ArrayList<>();
                        List<ItemStack> outputs = new ArrayList<>();
                        recipe.forEach((key, value) -> {
                            if (key <= 9) inputs.add(value);
                            else outputs.add(value);
                        });
                        // todo: output a copyable recipe config section

                        return false;
                    });
        }

        public AbstractItem recipe(int n) {
            return create()
                    .item(block -> {
                        var data = assertBlock(block, RecipeCopier.class);
                        if (data.getRecipe().get(n) != null) {
                            return ItemStackBuilder.of(data.getRecipe().get(n));
                        } else {
                            makeRecipe.notifyWindows();
                            return ItemStackBuilder.EMPTY;
                        }
                    })
                    .click((block, clickType, player, event) -> {
                        handleClick(event);

                        ItemStack currentItem = event.getCurrentItem();
                        var data = assertBlock(block, RecipeCopier.class);

                        if (currentItem != null && currentItem.getType() != Material.AIR) {
                            data.getRecipe().put(n, currentItem.clone());
                        } else {
                            data.getRecipe().remove(n);
                        }

                        return true;
                    });
        }
    }
}

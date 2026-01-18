package com.balugaq.runtimepylon.pylon.block;

import com.balugaq.runtimepylon.gui.ButtonSet;
import com.balugaq.runtimepylon.object.items.CustomItem;
import com.balugaq.runtimepylon.pylon.RuntimeKeys;
import com.balugaq.runtimepylon.pylon.block.base.WithModel;
import com.balugaq.runtimepylon.pylon.block.base.WithPage;
import com.balugaq.runtimepylon.pylon.block.base.WithPlaceable;
import com.balugaq.runtimepylon.pylon.block.base.WithRecipe;
import com.balugaq.runtimepylon.util.Keys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import kotlin.Pair;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.balugaq.runtimepylon.util.Lang.*;

@Getter
@Setter
@NullMarked
@Deprecated
public class ItemHub extends PylonBlock implements
                                        PylonGuiBlock,
                                        WithModel,
                                        WithPage,
                                        WithRecipe,
                                        WithPlaceable {
    public boolean placeable = false;
    public @Nullable ItemStack model = null;
    public @Nullable NamespacedKey itemId = null;
    public @Nullable NamespacedKey pageId = null;
    public @Nullable NamespacedKey recipeTypeId = null;
    public Map<Integer, ItemStack> recipe = new HashMap<>();

    public ItemHub(Block block, BlockCreateContext context) {
        super(block, context);
    }

    public ItemHub(Block block, PersistentDataContainer pdc) {
        super(block, pdc);
        placeable = pdc.getOrDefault(RuntimeKeys.placeable, PylonSerializers.BOOLEAN, true);
        model = pdc.get(RuntimeKeys.model, PylonSerializers.ITEM_STACK);
        itemId = pdc.get(RuntimeKeys.item_id, PylonSerializers.NAMESPACED_KEY);
        pageId = pdc.get(RuntimeKeys.page_id, PylonSerializers.NAMESPACED_KEY);
        recipeTypeId = pdc.get(RuntimeKeys.recipeType_id, PylonSerializers.NAMESPACED_KEY);
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
        pdc.set(RuntimeKeys.placeable, PylonSerializers.BOOLEAN, placeable);
        if (model != null) pdc.set(RuntimeKeys.model, PylonSerializers.ITEM_STACK, model);
        if (itemId != null) pdc.set(RuntimeKeys.item_id, PylonSerializers.NAMESPACED_KEY, itemId);
        if (pageId != null) pdc.set(RuntimeKeys.page_id, PylonSerializers.NAMESPACED_KEY, pageId);
        if (recipeTypeId != null) pdc.set(RuntimeKeys.recipeType_id, PylonSerializers.NAMESPACED_KEY, recipeTypeId);
        recipe.forEach((key, value) -> pdc.set(Keys.create("recipe" + key), PylonSerializers.ITEM_STACK, value));
    }

    @Override
    public Gui createGui() {
        ItemHubButtonSet<?> buttons = new ItemHubButtonSet<>(this);
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
                .addIngredient('e', buttons.setPage)
                .addIngredient('E', buttons.setRecipe)
                .addIngredient('d', buttons.unsetPage)
                .addIngredient('D', buttons.unsetRecipe)
                .addIngredient('k', buttons.setId)
                .addIngredient('g', buttons.page)
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
    public @NotNull Map<String, Inventory> createInventoryMapping() {
        return Map.of();
    }

    @Getter
    public static class ItemHubButtonSet<T extends ItemHub> extends ButtonSet<T> {
        public final AbstractItem
                registerItem,
                placeable;

        public ItemHubButtonSet(T b2) {
            super(b2);
            placeable = create()
                    .item(data -> {
                        if (data.isPlaceable()) {
                            return ItemStackBuilder.pylon(
                                    Material.LIME_STAINED_GLASS_PANE,
                                    RuntimeKeys.placeable_active
                            );
                        } else {
                            return ItemStackBuilder.pylon(
                                    Material.RED_STAINED_GLASS_PANE,
                                    RuntimeKeys.placeable_inactive
                            );
                        }
                    })
                    .click((data, clickType, player, event) -> {
                        data.setPlaceable(!data.isPlaceable());
                        done(player, placeable_1, data.isPlaceable());
                        return true;
                    });

            registerItem = create()
                    .item(data -> ItemStackBuilder.pylon(
                            Material.EMERALD_BLOCK,
                            RuntimeKeys.register_item
                    ))
                    .click((data, clickType, player, event) -> {
                        assertNotNull(data.getModel(), register_item_1);
                        assertTrue(!data.getModel().getType().isAir(), register_item_2);
                        assertNotNull(data.getItemId(), register_item_3);
                        if (assertBlock(data, WithPlaceable.class).isPlaceable()) {
                            assertTrue(data.getModel().getType().isBlock(), register_item_4);
                            CustomItem.register(CustomItem.class, ItemStackBuilder.pylon(data.getModel().getType(), data.getItemId()).build(), data.getItemId());
                            register(data.getItemId(), data.getModel().getType(), PylonBlock.class);
                        } else {
                            CustomItem.register(CustomItem.class, ItemStackBuilder.pylon(data.getModel().getType(), data.getItemId()).build());
                        }
                        done(player, register_item_5, data.getItemId());

                        return true;
                    });
        }
    }
}

package com.balugaq.runtimepylon.block;

import com.balugaq.runtimepylon.RuntimeKeys;
import com.balugaq.runtimepylon.block.base.WithFluidTag;
import com.balugaq.runtimepylon.block.base.WithModel;
import com.balugaq.runtimepylon.block.base.WithRecipe;
import com.balugaq.runtimepylon.gui.ButtonSet;
import com.balugaq.runtimepylon.item.fluid.PylonFluidTagHolder;
import com.balugaq.runtimepylon.util.Key;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.PylonFluidTag;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import kotlin.Pair;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.balugaq.runtimepylon.Lang.*;

// todo: page.addFluid
@Getter
public class FluidHub extends PylonBlock implements
        PylonGuiBlock,
        WithModel,
        WithRecipe,
        WithFluidTag {
    public @Nullable ItemStack model = null;
    public @Nullable ItemStack tag = null;
    public @Nullable NamespacedKey itemId = null;
    public @Nullable NamespacedKey recipeTypeId = null;
    public @NotNull Map<Integer, ItemStack> recipe = new HashMap<>();

    public FluidHub(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public FluidHub(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        model = pdc.get(RuntimeKeys.model, PylonSerializers.ITEM_STACK);
        tag = pdc.get(RuntimeKeys.tag, PylonSerializers.ITEM_STACK);
        itemId = pdc.get(RuntimeKeys.item_id, PylonSerializers.NAMESPACED_KEY);
        recipeTypeId = pdc.get(RuntimeKeys.recipeType_id, PylonSerializers.NAMESPACED_KEY);
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
        if (model != null) pdc.set(RuntimeKeys.model, PylonSerializers.ITEM_STACK, model);
        if (tag != null) pdc.set(RuntimeKeys.tag, PylonSerializers.ITEM_STACK, tag);
        if (itemId != null) pdc.set(RuntimeKeys.item_id, PylonSerializers.NAMESPACED_KEY, itemId);
        if (recipeTypeId != null) pdc.set(RuntimeKeys.recipeType_id, PylonSerializers.NAMESPACED_KEY, recipeTypeId);
        recipe.forEach((key, value) -> pdc.set(Key.create("recipe" + key), PylonSerializers.ITEM_STACK, value));
    }

    @Override
    public @NotNull Gui createGui() {
        FluidHubButtonSet<?> buttons = new FluidHubButtonSet<>(this);
        return Gui.normal()
                .setStructure(
                        "x x x x x x x x x",
                        ". k . e E I 1 2 3",
                        ". i . g t I 4 5 6",
                        ". s . d D O ! @ #",
                        ". . . . . O $ % ^",
                        "x x x x x x x x x"
                )
                .addIngredient('x', buttons.blackBackground)
                .addIngredient('.', buttons.grayBackground)
                .addIngredient('I', buttons.inputBorder)
                .addIngredient('O', buttons.outputBorder)
                .addIngredient('e', buttons.setTag)
                .addIngredient('E', buttons.setRecipe)
                .addIngredient('d', buttons.unsetTag)
                .addIngredient('D', buttons.unsetRecipe)
                .addIngredient('k', buttons.setId)
                .addIngredient('g', buttons.tag)
                .addIngredient('t', buttons.recipeType)
                .addIngredient('i', buttons.item)
                .addIngredient('1', buttons.recipe(1))
                .addIngredient('2', buttons.recipe(2))
                .addIngredient('3', buttons.recipe(3))
                .addIngredient('4', buttons.recipe(4))
                .addIngredient('5', buttons.recipe(5))
                .addIngredient('6', buttons.recipe(6))
                .addIngredient('!', buttons.recipe(1001))
                .addIngredient('@', buttons.recipe(1002))
                .addIngredient('#', buttons.recipe(1003))
                .addIngredient('$', buttons.recipe(1004))
                .addIngredient('%', buttons.recipe(1005))
                .addIngredient('^', buttons.recipe(1006))
                .addIngredient('s', buttons.registerFluid)
                .build();
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
    public @NotNull WithFluidTag setTag(@Nullable ItemStack tag) {
        this.tag = tag;
        return this;
    }

    public static class FluidHubButtonSet<T extends FluidHub> extends ButtonSet<T> {
        public @NotNull AbstractItem
                registerFluid,
                tag,
                setTag,
                unsetTag;

        public FluidHubButtonSet(@NotNull T b2) {
            super(b2);
            registerFluid = create()
                    .item(block -> ItemStackBuilder.pylonItem(
                            Material.EMERALD_BLOCK,
                            RuntimeKeys.register_fluid
                    ))
                    .click((data, clickType, player, event) -> {
                        assertNotNull(data.getItemId(), register_fluid_1);
                        assertNotNull(data.getModel(), register_fluid_2);
                        assertTrue(!data.getModel().getType().isAir(), register_fluid_3);

                        new PylonFluid(data.getItemId(), data.getModel().getType()).register();
                        done(player, register_fluid_4, data.getItemId());
                        return false;
                    });
            tag = create()
                    .item(data -> data.getTag() != null ? ItemStackBuilder.of(data.getTag()) : ItemStackBuilder.pylonItem(
                            Material.WHITE_STAINED_GLASS_PANE,
                            RuntimeKeys.tag
                    ))
                    .click((data, clickType, player, event) -> {
                        ItemStack currentItem = event.getCurrentItem();
                        PylonItem stack = PylonItem.fromStack(currentItem);
                        if (stack instanceof PylonFluidTagHolder<?> ds) {
                            ds.onClick(data, clickType, player, event, () -> {
                                data.setTag(currentItem);
                                reopen(player);
                            });
                        }

                        return true;
                    });
            setTag = create()
                    .item(block -> ItemStackBuilder.pylonItem(
                            Material.GOLD_BLOCK,
                            RuntimeKeys.set_tag
                    ))
                    .click((data, clickType, player, event) -> {
                        assertNotNull(data.getItemId(), set_tag_1);
                        PylonFluid fluid = assertNotNull(PylonRegistry.FLUIDS.get(data.getItemId()), set_tag_2);
                        PylonFluidTag tag = assertNotNull(parseTag(data.getTag()), set_tag_3);
                        fluid.addTag(tag);
                        done(player, set_tag_4, tag.getDisplayText(), data.getItemId());
                        return false;
                    });
            unsetTag = create()
                    .item(block -> ItemStackBuilder.pylonItem(
                            Material.IRON_BLOCK,
                            RuntimeKeys.unset_tag
                    ))
                    .click((data, clickType, player, event) -> {
                        assertNotNull(data.getItemId(), unset_tag_1);
                        PylonFluid fluid = assertNotNull(PylonRegistry.FLUIDS.get(data.getItemId()), unset_tag_2);
                        PylonFluidTag tag = assertNotNull(parseTag(data.getTag()), unset_tag_3);
                        fluid.removeTag(tag);
                        done(player, unset_tag_4, tag.getDisplayText(), data.getItemId());
                        return false;
                    });
        }

        public static @NotNull PylonFluidTag parseTag(@Nullable ItemStack tagItem) {
            PylonItem pylon = assertNotNull(PylonItem.fromStack(tagItem), parse_tag_1);
            assertTrue(pylon instanceof PylonFluidTagHolder<?>, parse_tag_2);
            return ((PylonFluidTagHolder<?>) pylon).getTag();
        }
    }
}

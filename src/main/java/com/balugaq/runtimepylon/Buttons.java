package com.balugaq.runtimepylon;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import static com.balugaq.runtimepylon.GuiItem.*;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

public class Buttons <T extends PylonBlock & PylonGuiBlock> {
    public static <T extends PylonBlock & PylonGuiBlock> ClickHandler<T> deny() {
        return (data, clickType, player, event) -> {
            event.setCancelled(true);
        };
    }

    public static <T extends PylonBlock & PylonGuiBlock> ClickHandler<T> allow() {
        return (data, clickType, player, event) -> {};
    }

    public T block;
    public Buttons(@NotNull T block) {
        this.block = block;
    }

    public GuiItem<T> create() {
        return GuiItem.create(block);
    }

    public final AbstractItem

    blackBackground = create()
            .item(ItemStackBuilder.pylonItem(
                    Material.BLACK_STAINED_GLASS_PANE,
                    Key.create("black_background")
            ))
            .click(deny()),

    grayBackground = create()
            .item(ItemStackBuilder.pylonItem(
                    Material.GRAY_STAINED_GLASS_PANE,
                    Key.create("gray_background")
            ))
            .click(deny()),
    
    setItemGroup = create()
            .item(ItemStackBuilder.EMPTY)
            .click((block, clickType, player, event) -> {
                var data = assertBlock(block, WithGroup.class);

                assertNotNull(data.getItemId(), "Not set item id");
                assertNotNull(data.getGroupId(), "Not set group id");

                Map<NamespacedKey, SimpleStaticGuidePage> pages = RuntimePylon.getGuidePages();
                SimpleStaticGuidePage page = assertNotNull(pages.get(data.getGroupId()), "Unknown group");

                assertNotNull(PylonRegistry.ITEMS.get(data.getItemId()), "Unknown item");
                page.addItem(data.getItemId());

                done(player, "Added {} to {}", data.getItemId(), data.getGroupId());
            }),

    setRecipe = create()
            .item(ItemStackBuilder.EMPTY)
            .click((block, clickType, player, event) -> {
                var data = assertBlock(block, WithRecipe.class);
                assertNotNull(data.getItemId(), "Not set item id");
                assertNotNull(data.getRecipeTypeId(), "Not set recipe type id");

                RecipeType<? extends PylonRecipe> recipeType = assertNotNull(PylonRegistry.RECIPE_TYPES.get(data.getRecipeTypeId()), "Unknown recipe type");
                if (!(recipeType.getClass().getGenericSuperclass() instanceof ParameterizedType pt)) {
                    return;
                }

                Class<? extends PylonRecipe> pylonRecipeClass = (Class<? extends PylonRecipe>) pt.getActualTypeArguments()[0];
                var adapter = assertNotNull(RecipeAdapter.find(recipeType, pylonRecipeClass), "Incompatible recipe type");
                assertTrue(adapter.apply(data.getItemId(), data.getRecipe()), "Incompatible recipe");
            }),

    unsetItemGroup = create()
        .item(ItemStackBuilder.EMPTY)
        .click((block, clickType, player, event) -> {
            var data = assertBlock(block, WithGroup.class);
            assertNotNull(data.getItemId(), "Not set item id");
            assertNotNull(data.getGroupId(), "Not set group id");
            Map<NamespacedKey, SimpleStaticGuidePage> pages = RuntimePylon.getGuidePages();
            SimpleStaticGuidePage page = assertNotNull(pages.get(data.getGroupId()), "Unknown group");
            page.getButtons().removeIf(item -> {
                if (!(item instanceof ItemButton button)) {
                    return false;
                }

                PylonItem pylon = PylonItem.fromStack(button.getStack());
                if (pylon == null) return false;

                return pylon.getKey().equals(data.getItemId());
            });
        }),

    unsetRecipe = create()
        .item(ItemStackBuilder.EMPTY)
        .click((block, clickType, player, event) -> {
            var data = assertBlock(block, WithRecipe.class);
            assertNotNull(data.getItemId(), "Not set item id");
            assertNotNull(data.getRecipeTypeId(), "Not set recipe type id");

            RecipeType<? extends PylonRecipe> recipeType = assertNotNull(PylonRegistry.RECIPE_TYPES.get(data.getRecipeTypeId()), "Unknown recipe type");
            if (!(recipeType.getClass().getGenericSuperclass() instanceof ParameterizedType pt)) {
                return;
            }

            Class<? extends PylonRecipe> pylonRecipeClass = (Class<? extends PylonRecipe>) pt.getActualTypeArguments()[0];
            RecipeAdapter<? extends PylonRecipe> adapter = assertNotNull(RecipeAdapter.find(recipeType, pylonRecipeClass), "Incompatible recipe type");
            adapter.removeRecipe(data.getItemId());
        }),

    setId = create()
            .item(ItemStackBuilder.EMPTY)
            .click((block, clickType, player, event) -> {
                var data = assertBlock(block, WithModel.class);

                waitInput(player, "Enter item id", itemId -> {
                    data.setItemId(assertNotNull(toNamespacedKey(itemId), "Invalid item id"));
                });
            }),

    itemGroup = create()
            .item(ItemStackBuilder.EMPTY)
            .click((block, clickType, player, event) -> {
                var data = assertBlock(block, WithGroup.class);

                if (clickType.isLeftClick()) {
                    if (clickType.isShiftClick()) {
                        waitInput(player, "Enter group id", groupId -> {
                            data.setGroupId(assertNotNull(toNamespacedKey(groupId), "Invalid group id"));
                        });
                    } else {
                        SearchPages.GROUP.open(player, group -> data.setGroupId(group.getKey()));
                    }
                } else if (clickType.isRightClick()) {
                    assertNotNull(data.getGroupId(), "Not set group id yet");
                }
            });
}

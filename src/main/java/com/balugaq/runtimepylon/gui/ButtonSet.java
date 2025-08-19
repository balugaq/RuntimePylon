package com.balugaq.runtimepylon.gui;

import com.balugaq.runtimepylon.item.DataStack;
import com.balugaq.runtimepylon.item.NumberStack;
import com.balugaq.runtimepylon.item.StringStack;
import com.balugaq.runtimepylon.util.Key;
import com.balugaq.runtimepylon.util.RecipeAdapter;
import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.block.base.WithGroup;
import com.balugaq.runtimepylon.block.base.WithModel;
import com.balugaq.runtimepylon.block.base.WithRecipe;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.window.Window;

import static com.balugaq.runtimepylon.gui.GuiItem.*;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Optional;

@Getter
public class ButtonSet<T extends PylonBlock & PylonGuiBlock> {
    public static <T extends PylonBlock & PylonGuiBlock> ClickHandler<T> deny() {
        return (data, clickType, player, event) -> {
            event.setCancelled(true);
            return false;
        };
    }

    public static <T extends PylonBlock & PylonGuiBlock> ClickHandler<T> allow() {
        return (data, clickType, player, event) -> false;
    }

    @NotNull
    public static Component displayName(@NotNull ItemStack itemStack) {
        return Optional.ofNullable(itemStack.getData(DataComponentTypes.ITEM_NAME)).orElse(Component.text(""));
    }

    public final T block;
    public final AbstractItem
            blackBackground,
            grayBackground,
            setItemGroup,
            setRecipe,
            unsetItemGroup,
            unsetRecipe,
            setId,
            itemGroup,
            recipeType,
            item;
    public ButtonSet(@NotNull T b2) {
        this.block = b2;
        blackBackground = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.BLACK_STAINED_GLASS_PANE,
                        Key.create("black_background")
                ))
                .click(deny());

        grayBackground = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.GRAY_STAINED_GLASS_PANE,
                        Key.create("gray_background")
                ))
                .click(deny());

        setItemGroup = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.GREEN_STAINED_GLASS_PANE,
                        Key.create("set_item_group")
                ))
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithGroup.class);

                    assertNotNull(data.getItemId(), "Not set item id");
                    assertNotNull(data.getGroupId(), "Not set group id");

                    Map<NamespacedKey, SimpleStaticGuidePage> pages = RuntimePylon.getGuidePages();
                    SimpleStaticGuidePage page = assertNotNull(pages.get(data.getGroupId()), "Unknown group");

                    assertNotNull(PylonRegistry.ITEMS.get(data.getItemId()), "Unknown item");
                    page.addItem(data.getItemId());

                    done(player, "Added {} to {}", data.getItemId(), data.getGroupId());
                    return true;
                });

        setRecipe = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.GREEN_STAINED_GLASS_PANE,
                        Key.create("set_recipe")
                ))
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithRecipe.class);
                    assertNotNull(data.getItemId(), "Not set item id");
                    assertNotNull(data.getModel(), "Not set model");
                    assertNotNull(data.getRecipeTypeId(), "Not set recipe type id");

                    RecipeType<? extends PylonRecipe> recipeType = assertNotNull(PylonRegistry.RECIPE_TYPES.get(data.getRecipeTypeId()), "Unknown recipe type");
                    if (!(recipeType.getClass().getGenericSuperclass() instanceof ParameterizedType pt)) {
                        return false;
                    }

                    Class<? extends PylonRecipe> pylonRecipeClass = (Class<? extends PylonRecipe>) pt.getActualTypeArguments()[0];
                    var adapter = assertNotNull(RecipeAdapter.find(recipeType, pylonRecipeClass), "Incompatible recipe type");
                    assertTrue(adapter.noRecipe(data.getItemId(), data.getModel(), data.getRecipe()), "Recipe already exists");
                    assertTrue(adapter.apply(data.getItemId(), data.getModel(), data.getRecipe()), "Incompatible recipe");
                    return true;
                });

        unsetItemGroup = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.RED_STAINED_GLASS_PANE,
                        Key.create("unset_item_group")
                ))
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
                    return true;
                });

        unsetRecipe = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.RED_STAINED_GLASS_PANE,
                        Key.create("unset_recipe")
                ))
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithRecipe.class);
                    assertNotNull(data.getItemId(), "Not set item id");
                    assertNotNull(data.getRecipeTypeId(), "Not set recipe type id");

                    RecipeType<? extends PylonRecipe> recipeType = assertNotNull(PylonRegistry.RECIPE_TYPES.get(data.getRecipeTypeId()), "Unknown recipe type");
                    if (!(recipeType.getClass().getGenericSuperclass() instanceof ParameterizedType pt)) {
                        return false;
                    }

                    Class<? extends PylonRecipe> pylonRecipeClass = (Class<? extends PylonRecipe>) pt.getActualTypeArguments()[0];
                    RecipeAdapter<? extends PylonRecipe> adapter = assertNotNull(RecipeAdapter.find(recipeType, pylonRecipeClass), "Incompatible recipe type");
                    adapter.removeRecipe(data.getItemId());

                    return true;
                });

        setId = create()
                .item(block -> {
                    var data = assertBlock(block, WithModel.class);
                    var itemId = data.getItemId();
                    if (itemId == null) {
                        return ItemStackBuilder.pylonItem(
                                Material.BLUE_STAINED_GLASS_PANE,
                                Key.create("set_id")
                        );
                    } else {
                        return ItemStackBuilder.pylonItem(
                                Material.BLUE_STAINED_GLASS_PANE,
                                Key.create("set_id")
                        ).lore("Current item id: " + data.getItemId());
                    }
                })
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithModel.class);

                    waitInput(player, "Enter item id", itemId -> {
                        data.setItemId(assertNotNull(toNamespacedKey(itemId), "Invalid item id"));
                    });

                    return true;
                });

        itemGroup = create()
                .item(block -> {
                    var data = assertBlock(block, WithGroup.class);
                    if (data.getGroupId() == null) {
                        return ItemStackBuilder.pylonItem(
                                Material.WHITE_STAINED_GLASS_PANE,
                                Key.create("item_group")
                        );
                    } else {
                        return RuntimePylon.getGuidePages().get(data.getGroupId()).getItem();
                    }
                })
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithGroup.class);

                    if (clickType.isLeftClick()) {
                        if (clickType.isShiftClick()) {
                            waitInput(player, "Enter group id", groupId -> {
                                data.setGroupId(assertNotNull(toNamespacedKey(groupId), "Invalid group id"));
                            });
                        } else {
                            SearchPages.openGroupSearchPage(player, group -> {
                                data.setGroupId(group.getKey());
                                done(player, "Set group id to {}", group.getKey());
                                reopen(player);
                            });
                        }
                    } else if (clickType.isRightClick()) {
                        assertNotNull(data.getGroupId(), "Not set group id yet");
                        // copy id

                        player.sendMessage(Component.text()
                                .content("Copied group id")
                                .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))
                                .clickEvent(ClickEvent.copyToClipboard(data.getGroupId().toString())));
                    }

                    return true;
                });

        recipeType = create()
                .item(block -> {
                    var data = assertBlock(block, WithRecipe.class);

                    if (data.getRecipeTypeId() == null) {
                        return ItemStackBuilder.pylonItem(
                                Material.WHITE_STAINED_GLASS_PANE,
                                Key.create("recipe_type")
                        );
                    } else {
                        return ItemStackBuilder.pylonItem(
                                Material.CRAFTING_TABLE,
                                Key.create("recipe_type")
                        ).lore("Current recipe type id: " + data.getRecipeTypeId());
                    }
                })
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithRecipe.class);
                    if (clickType.isLeftClick()) {
                        if (clickType.isShiftClick()) {
                            waitInput(player, "Enter recipe type id", recipeTypeId -> {
                                data.setRecipeTypeId(assertNotNull(toNamespacedKey(recipeTypeId), "Invalid recipe type id"));
                            });
                        } else {
                            SearchPages.openRecipeTypeSearchPage(player, recipeType -> {
                                data.setRecipeTypeId(recipeType.getKey());
                                done(player, "Set recipe type id to {}", recipeType.getKey());
                                reopen(player);
                            });
                        }
                    } else if (clickType.isRightClick()) {
                        assertNotNull(data.getRecipeTypeId(), "Not set recipe type id yet");
                        // copy id

                        player.sendMessage(Component.text()
                                .content("Copied recipe type id")
                                .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))
                                .clickEvent(ClickEvent.copyToClipboard(data.getRecipeTypeId().toString()))
                        );
                    }

                    return true;
                });

        item = create()
                .item(block -> {
                    var data = assertBlock(block, WithModel.class);
                    var model = data.getModel();

                    if (model == null || model.getType() == Material.AIR) {
                        return ItemStackBuilder.pylonItem(
                                Material.WHITE_STAINED_GLASS_PANE,
                                Key.create("item")
                        );
                    } else {
                        return ItemStackBuilder.of(model);
                    }
                })
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithModel.class);
                    event.setCancelled(true);
                    ItemStack cursor = event.getCursor().clone();
                    if (data.getModel() != null) {
                        player.setItemOnCursor(data.getModel().clone());
                    }
                    data.setModel(cursor);
                    done(player, Component.text("Set item to ").append(displayName(cursor)));

                    PylonItem pylon = PylonItem.fromStack(cursor);
                    if (pylon != null) {
                        data.setItemId(pylon.getKey());
                        done(player, "Set item id to {}", pylon.getKey());
                    }

                    return true;
                });
    }

    public GuiItem<T> create() {
        return GuiItem.create(block);
    }

    public AbstractItem recipe(int n) {
        return create()
                .item(block -> {
                    var data = assertBlock(block, WithRecipe.class);
                    if (data.getRecipe().get(n) != null) {
                        return ItemStackBuilder.of(data.getRecipe().get(n));
                    } else {
                        return ItemStackBuilder.EMPTY;
                    }
                })
                .click((block, clickType, player, event) -> {
                    PylonItem stack = PylonItem.fromStack(event.getCurrentItem());
                    if (stack instanceof DataStack data) {
                        data.onClick(block, clickType, player, event);
                        return true;
                    }

                    var data = assertBlock(block, WithRecipe.class);

                    event.setCancelled(true);
                    ItemStack cursor = event.getCursor().clone();
                    if (data.getRecipe().get(n) != null) {
                        player.setItemOnCursor(data.getRecipe().get(n).clone());
                    }
                    if (cursor != null && cursor.getType() != Material.AIR) {
                        data.getRecipe().put(n, cursor);
                        done(player, Component.text("Set recipe #").append(Component.text(n)).append(Component.text(" to ")).append(displayName(cursor)));
                    } else {
                        data.getRecipe().remove(n);
                        done(player, "Unset recipe #{}", n);
                    }

                    return true;
                });
    }

    public void reopen(@NotNull Player player) {
        Window.single()
                .setGui(getBlock().getGui())
                .setTitle(new AdventureComponentWrapper(getBlock().getName()))
                .setViewer(player)
                .build()
                .open();
    }
}

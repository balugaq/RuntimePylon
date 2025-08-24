package com.balugaq.runtimepylon.gui;

import com.balugaq.runtimepylon.pylon.RuntimeKeys;
import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.pylon.block.base.WithModel;
import com.balugaq.runtimepylon.pylon.block.base.WithPage;
import com.balugaq.runtimepylon.pylon.block.base.WithRecipe;
import com.balugaq.runtimepylon.gui.interact.WrongStateException;
import com.balugaq.runtimepylon.pylon.item.DataStack;
import com.balugaq.runtimepylon.pylon.page.SearchPages;
import com.balugaq.runtimepylon.util.RecipeAdapter;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.window.Window;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Optional;

import static com.balugaq.runtimepylon.util.Lang.*;
import static com.balugaq.runtimepylon.gui.GuiItem.toNamespacedKey;
import static com.balugaq.runtimepylon.gui.GuiItem.waitInput;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
@Getter
public class ButtonSet<T extends PylonBlock & PylonGuiBlock> {
    public final @NotNull T block;
    public @NotNull AbstractItem
            blackBackground,
            grayBackground,
            inputBorder,
            outputBorder,
            setPage,
            setRecipe,
            unsetPage,
            unsetRecipe,
            setId,
            page,
            recipeType,
            item;

    public ButtonSet(@NotNull T b2) {
        this.block = b2;
        blackBackground = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.BLACK_STAINED_GLASS_PANE,
                        RuntimeKeys.black_background
                ))
                .click(deny());

        grayBackground = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.GRAY_STAINED_GLASS_PANE,
                        RuntimeKeys.gray_background
                ))
                .click(deny());

        inputBorder = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.BLUE_STAINED_GLASS_PANE,
                        RuntimeKeys.input_border
                ))
                .click(deny());

        outputBorder = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.ORANGE_STAINED_GLASS_PANE,
                        RuntimeKeys.output_border
                ))
                .click(deny());

        setPage = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.GREEN_STAINED_GLASS_PANE,
                        RuntimeKeys.set_page
                ))
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithPage.class);

                    assertNotNull(data.getItemId(), set_page_1);
                    assertNotNull(data.getPageId(), set_page_2);

                    Map<NamespacedKey, SimpleStaticGuidePage> pages = RuntimePylon.getGuidePages();
                    SimpleStaticGuidePage page = assertNotNull(pages.get(data.getPageId()), set_page_3);

                    assertNotNull(PylonRegistry.ITEMS.get(data.getItemId()), set_page_4);
                    page.addItem(data.getItemId());

                    done(player, set_page_5, data.getItemId(), data.getPageId());
                    return true;
                });

        setRecipe = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.GREEN_STAINED_GLASS_PANE,
                        RuntimeKeys.set_recipe
                ))
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithRecipe.class);
                    assertNotNull(data.getItemId(), set_recipe_1);
                    assertNotNull(data.getModel(), set_recipe_2);
                    assertNotNull(data.getRecipeTypeId(), set_recipe_3);

                    RecipeType<? extends PylonRecipe> recipeType = assertNotNull(PylonRegistry.RECIPE_TYPES.get(data.getRecipeTypeId()), set_recipe_4);
                    if (!(recipeType.getClass().getGenericSuperclass() instanceof ParameterizedType pt)) {
                        return false;
                    }

                    Class<? extends PylonRecipe> pylonRecipeClass = (Class<? extends PylonRecipe>) pt.getActualTypeArguments()[0];
                    var adapter = assertNotNull(RecipeAdapter.find(recipeType, pylonRecipeClass), set_recipe_5);
                    assertTrue(adapter.noRecipe(data.getItemId(), data.getModel(), data.getRecipe()), set_recipe_6);
                    assertTrue(adapter.apply(data.getItemId(), data.getModel(), data.getRecipe()), set_recipe_7);
                    done(player, Component.text(set_recipe_8).append(data.getModel().displayName()));
                    return true;
                });

        unsetPage = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.RED_STAINED_GLASS_PANE,
                        RuntimeKeys.unset_page
                ))
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithPage.class);
                    assertNotNull(data.getItemId(), unset_page_1);
                    assertNotNull(data.getPageId(), unset_page_2);
                    Map<NamespacedKey, SimpleStaticGuidePage> pages = RuntimePylon.getGuidePages();
                    SimpleStaticGuidePage page = assertNotNull(pages.get(data.getPageId()), unset_page_3);
                    page.getButtons().removeIf(item -> {
                        if (!(item instanceof ItemButton button)) {
                            return false;
                        }

                        PylonItem pylon = PylonItem.fromStack(button.getStack());
                        if (pylon == null) return false;

                        return pylon.getKey().equals(data.getItemId());
                    });
                    done(player, unset_page_4, data.getItemId(), data.getPageId());
                    return true;
                });

        unsetRecipe = create()
                .item(block -> ItemStackBuilder.pylonItem(
                        Material.RED_STAINED_GLASS_PANE,
                        RuntimeKeys.unset_recipe
                ))
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithRecipe.class);
                    assertNotNull(data.getItemId(), unset_recipe_1);
                    assertNotNull(data.getRecipeTypeId(), unset_recipe_2);

                    RecipeType<? extends PylonRecipe> recipeType = assertNotNull(PylonRegistry.RECIPE_TYPES.get(data.getRecipeTypeId()), unset_recipe_3);
                    if (!(recipeType.getClass().getGenericSuperclass() instanceof ParameterizedType pt)) {
                        return false;
                    }

                    Class<? extends PylonRecipe> pylonRecipeClass = (Class<? extends PylonRecipe>) pt.getActualTypeArguments()[0];
                    RecipeAdapter<? extends PylonRecipe> adapter = assertNotNull(RecipeAdapter.find(recipeType, pylonRecipeClass), unset_recipe_4);
                    adapter.removeRecipe(data.getItemId());
                    done(player, unset_recipe_5, data.getItemId(), data.getRecipeTypeId());

                    return true;
                });

        setId = create()
                .item(block -> {
                    var data = assertBlock(block, WithModel.class);
                    var itemId = data.getItemId();
                    if (itemId == null) {
                        return ItemStackBuilder.pylonItem(
                                Material.BLUE_STAINED_GLASS_PANE,
                                RuntimeKeys.set_id
                        );
                    } else {
                        return ItemStackBuilder.pylonItem(
                                Material.BLUE_STAINED_GLASS_PANE,
                                RuntimeKeys.set_id
                        ).lore(set_id_1 + data.getItemId());
                    }
                })
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithModel.class);

                    waitInput(player, set_id_2, itemId -> {
                        if (itemId.contains(":") && !itemId.startsWith(RuntimePylon.getInstance().getName().toLowerCase())) {
                            throw new WrongStateException(set_id_3 + RuntimePylon.getInstance().getName().toLowerCase());
                        } else {
                            data.setItemId(assertNotNull(toNamespacedKey(itemId), set_id_4));
                            reopen(player);
                        }
                    });

                    return true;
                });

        page = create()
                .item(block -> {
                    var data = assertBlock(block, WithPage.class);
                    if (data.getPageId() == null) {
                        return ItemStackBuilder.pylonItem(
                                Material.WHITE_STAINED_GLASS_PANE,
                                RuntimeKeys.page
                        );
                    } else {
                        return RuntimePylon.getGuidePages().get(data.getPageId()).getItem();
                    }
                })
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithPage.class);

                    if (clickType.isLeftClick()) {
                        if (clickType.isShiftClick()) {
                            waitInput(player, page_1, pageId -> {
                                data.setPageId(assertNotNull(toNamespacedKey(pageId), page_2));
                            });
                        } else {
                            SearchPages.openPageSearchPage(player, page -> {
                                data.setPageId(page.getKey());
                                done(player, page_3, page.getKey());
                                reopen(player);
                            });
                        }
                    } else if (clickType.isRightClick()) {
                        assertNotNull(data.getPageId(), page_4);
                        // copy id

                        player.sendMessage(Component.text()
                                .content(page_5)
                                .hoverEvent(HoverEvent.showText(Component.text(page_6)))
                                .clickEvent(ClickEvent.copyToClipboard(data.getPageId().toString())));
                    }

                    return true;
                });

        recipeType = create()
                .item(block -> {
                    var data = assertBlock(block, WithRecipe.class);

                    if (data.getRecipeTypeId() == null) {
                        return ItemStackBuilder.pylonItem(
                                Material.WHITE_STAINED_GLASS_PANE,
                                RuntimeKeys.recipe_type
                        );
                    } else {
                        return ItemStackBuilder.pylonItem(
                                Material.CRAFTING_TABLE,
                                RuntimeKeys.recipe_type
                        ).lore(recipe_type_1 + data.getRecipeTypeId());
                    }
                })
                .click((block, clickType, player, event) -> {
                    var data = assertBlock(block, WithRecipe.class);
                    if (clickType.isLeftClick()) {
                        if (clickType.isShiftClick()) {
                            waitInput(player, recipe_type_2, recipeTypeId -> {
                                data.setRecipeTypeId(assertNotNull(toNamespacedKey(recipeTypeId), recipe_type_3));
                            });
                        } else {
                            SearchPages.openRecipeTypeSearchPage(player, recipeType -> {
                                data.setRecipeTypeId(recipeType.getKey());
                                done(player, recipe_type_4, recipeType.getKey());
                                reopen(player);
                            });
                        }
                    } else if (clickType.isRightClick()) {
                        assertNotNull(data.getRecipeTypeId(), recipe_type_5);
                        // copy id

                        player.sendMessage(Component.text()
                                .content(recipe_type_6)
                                .hoverEvent(HoverEvent.showText(Component.text(recipe_type_7)))
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
                        return ItemStackBuilder.EMPTY;
                    } else {
                        return ItemStackBuilder.of(model);
                    }
                })
                .click((block, clickType, player, event) -> {
                    handleClick(event);

                    var data = assertBlock(block, WithModel.class);
                    ItemStack currentItem = event.getCurrentItem();
                    if (currentItem == null) {
                        data.setModel(null);
                        return true;
                    }
                    data.setModel(currentItem);
                    done(player, Component.text(item_1).append(displayName(currentItem)));

                    PylonItem pylon = PylonItem.fromStack(currentItem);
                    if (pylon != null) {
                        data.setItemId(pylon.getKey());
                        done(player, item_2, pylon.getKey());
                    }

                    return true;
                });
    }

    public static <T extends PylonBlock & PylonGuiBlock> @NotNull ClickHandler<T> deny() {
        return (data, clickType, player, event) -> {
            event.setCancelled(true);
            return false;
        };
    }

    public static <T extends PylonBlock & PylonGuiBlock> @NotNull ClickHandler<T> allow() {
        return (data, clickType, player, event) -> false;
    }

    @NotNull
    public static Component displayName(@NotNull ItemStack itemStack) {
        return Optional.ofNullable(itemStack.getData(DataComponentTypes.ITEM_NAME)).orElse(Component.text(""));
    }

    public static <T> @NotNull T assertNotNull(@Nullable T o) {
        return GuiItem.assertNotNull(o);
    }

    public static <T> @NotNull T assertNotNull(@Nullable T o, @NotNull String message) {
        return GuiItem.assertNotNull(o, message);
    }

    public static void assertTrue(boolean stmt, @NotNull String message) {
        GuiItem.assertTrue(stmt, message);
    }

    public static void assertFalse(boolean stmt, @NotNull String message) {
        GuiItem.assertFalse(stmt, message);
    }

    public static void done(@NotNull Player player, @NotNull String literal, @NotNull Object... args) {
        GuiItem.done(player, literal, args);
    }

    public static void done(@NotNull Player player, @NotNull ComponentLike component) {
        GuiItem.done(player, component);
    }

    public static <T extends PylonBlock & PylonGuiBlock, K> @NotNull K assertBlock(@NotNull T block, @NotNull Class<K> expected) {
        return GuiItem.assertBlock(block, expected);
    }

    public static boolean isOutput(int n) {
        return n > 1000;
    }

    public @NotNull GuiItem<T> create() {
        return GuiItem.create(block);
    }

    public AbstractItem recipe(int n) {
        return create()
                .item(block -> {
                    var data = assertBlock(block, WithRecipe.class);
                    if (data.getRecipe().get(n) != null) {
                        return ItemStackBuilder.of(data.getRecipe().get(n));
                    } else {
                        getItem().notifyWindows();
                        return ItemStackBuilder.EMPTY;
                    }
                })
                .click((block, clickType, player, event) -> {
                    handleClick(event);

                    ItemStack currentItem = event.getCurrentItem();
                    PylonItem stack = PylonItem.fromStack(currentItem);
                    if (stack instanceof DataStack data) {
                        data.onClick(block, clickType, player, event, () -> reopen(player));
                    }

                    var data = assertBlock(block, WithRecipe.class);

                    if (currentItem != null && currentItem.getType() != Material.AIR) {
                        data.getRecipe().put(n, currentItem.clone());
                    } else {
                        data.getRecipe().remove(n);
                    }

                    return true;
                });
    }

    public void reopen(@NotNull Player player) {
        RuntimePylon.runTaskLater(() -> {
            Window.single()
                    .setGui(getBlock().getGui())
                    .setTitle(new AdventureComponentWrapper(PylonRegistry.ITEMS.get(getBlock().getKey()).getItemStack().displayName()))
                    .setViewer(player)
                    .build()
                    .open();
        }, 1L);
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        Player player = (Player) event.getWhoClicked();
        switch (event.getClick()) {
            case ClickType.RIGHT -> {
                if (current != null) {
                    if (cursor.isSimilar(current)) {
                        if (current.getAmount() < current.getMaxStackSize()) {
                            current.setAmount(current.getAmount() + 1);
                            cursor.setAmount(cursor.getAmount() - 1);
                        }
                    } else if (cursor.getType().isAir()) {
                        int a = current.getAmount() / 2; // ->current
                        int b = current.getAmount() - a; // ->cursor
                        event.setCurrentItem(current.asQuantity(a));
                        player.setItemOnCursor(current.asQuantity(b));
                    }
                } else {
                    event.setCurrentItem(cursor.asQuantity(1));
                    cursor.setAmount(cursor.getAmount() - 1);
                }
            }
            case ClickType.MIDDLE -> {
                if (current != null && cursor.getType().isAir() && cursor.getType().isAir()) {
                    player.setItemOnCursor(current.clone().asQuantity(current.getMaxStackSize()));
                }
            }
            case ClickType.CONTROL_DROP -> {
                if (current != null) {
                    player.getWorld().dropItemNaturally(player.getEyeLocation(), current.clone());
                    event.setCurrentItem(ItemStackBuilder.EMPTY.get());
                }
            }
            case ClickType.DROP -> {
                if (current != null) {
                    player.getWorld().dropItemNaturally(player.getEyeLocation(), current.asOne());
                    current.setAmount(current.getAmount() - 1);
                }
            }
            case ClickType.SWAP_OFFHAND -> {
                // swap(current, offhand);
                ItemStack offhand = player.getInventory().getItemInOffHand().clone();
                player.getInventory().setItemInOffHand(current);
                event.setCurrentItem(offhand);
            }
            case ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT -> {
                if (current != null) {
                    player.getInventory().addItem(current);
                }
            }
            default -> {
                if (current != null && cursor.isSimilar(current)) {
                    if (current.getAmount() < current.getMaxStackSize()) {
                        int moved = Math.min(cursor.getAmount(), current.getMaxStackSize() - current.getAmount());
                        current.setAmount(current.getAmount() + moved);
                        cursor.setAmount(cursor.getAmount() - moved);
                    }
                } else {
                    // swap(current, cursor);
                    ItemStack c = cursor.clone();
                    player.setItemOnCursor(current);
                    event.setCurrentItem(c);
                }
            }
        }
    }
}

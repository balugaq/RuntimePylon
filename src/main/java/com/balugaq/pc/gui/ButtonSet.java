package com.balugaq.pc.gui;

import com.balugaq.pc.PylonCustomizer;
import com.balugaq.pc.pylon.PylonCustomizerKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.window.Window;

import java.util.Optional;

/**
 * @author balugaq
 */
@SuppressWarnings({"unchecked", "UnstableApiUsage"})
@Getter
@NullMarked
public class ButtonSet<T extends PylonBlock & PylonGuiBlock> {
    public final T block;
    public AbstractItem
            blackBackground,
            grayBackground,
            inputBorder,
            outputBorder;

    public ButtonSet(T b2) {
        this.block = b2;
        blackBackground = create()
                .item(block -> ItemStackBuilder.pylon(
                        Material.BLACK_STAINED_GLASS_PANE,
                        PylonCustomizerKeys.black_background
                ))
                .click(deny());

        grayBackground = create()
                .item(block -> ItemStackBuilder.pylon(
                        Material.GRAY_STAINED_GLASS_PANE,
                        PylonCustomizerKeys.gray_background
                ))
                .click(deny());

        inputBorder = create()
                .item(block -> ItemStackBuilder.pylon(
                        Material.BLUE_STAINED_GLASS_PANE,
                        PylonCustomizerKeys.input_border
                ))
                .click(deny());

        outputBorder = create()
                .item(block -> ItemStackBuilder.pylon(
                        Material.ORANGE_STAINED_GLASS_PANE,
                        PylonCustomizerKeys.output_border
                ))
                .click(deny());
    }

    public GuiItem<T> create() {
        return GuiItem.create(block);
    }

    public static <T extends PylonBlock & PylonGuiBlock> ClickHandler<T> deny() {
        return (data, clickType, player, event) -> {
            event.setCancelled(true);
            return false;
        };
    }

    public static <T extends PylonBlock & PylonGuiBlock, K> K assertBlock(T block, Class<K> expected) {
        return GuiItem.assertBlock(block, expected);
    }

    public static <T> T assertNotNull(@Nullable T o, String message) {
        return GuiItem.assertNotNull(o, message);
    }

    public static void done(Player player, String literal, Object... args) {
        GuiItem.done(player, literal, args);
    }

    public static void assertTrue(boolean stmt, String message) {
        GuiItem.assertTrue(stmt, message);
    }

    public static void done(Player player, ComponentLike component) {
        GuiItem.done(player, component);
    }

    public void reopen(Player player) {
        PylonCustomizer.runTaskLater(
                () -> {
                    Window.single()
                            .setGui(getBlock().getGui())
                            .setTitle(new AdventureComponentWrapper(PylonRegistry.ITEMS.get(getBlock().getKey()).getItemStack().displayName()))
                            .setViewer(player)
                            .build()
                            .open();
                }, 1L
        );
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

    public static Component displayName(ItemStack itemStack) {
        return Optional.ofNullable(itemStack.getData(DataComponentTypes.ITEM_NAME)).orElse(Component.text(""));
    }

    public static <T extends PylonBlock & PylonGuiBlock> ClickHandler<T> allow() {
        return (data, clickType, player, event) -> false;
    }

    public static <T> T assertNotNull(@Nullable T o) {
        return GuiItem.assertNotNull(o);
    }

    public static void assertFalse(boolean stmt, String message) {
        GuiItem.assertFalse(stmt, message);
    }

    public static boolean isOutput(int n) {
        return n > 1000;
    }
}

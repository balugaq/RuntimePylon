package com.balugaq.pc.gui;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.item.Item;

/**
 * @author balugaq
 */
@FunctionalInterface
@NullMarked
public interface ClickHandler<T extends PylonBlock & PylonGuiBlock> {
    /**
     * A method called if the {@link ItemStack} associated to this {@link Item} has been clicked by a player.
     *
     * @param clickType
     *         The {@link ClickType} the {@link Player} performed.
     * @param player
     *         The {@link Player} who clicked on the {@link ItemStack}.
     * @param event
     *         The {@link InventoryClickEvent} associated with this click.
     */
    boolean handleClick(T block, ClickType clickType, Player player, InventoryClickEvent event);
}

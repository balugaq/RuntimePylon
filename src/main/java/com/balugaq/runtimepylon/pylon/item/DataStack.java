package com.balugaq.runtimepylon.pylon.item;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
@Deprecated
public interface DataStack {
    <T extends PylonBlock & PylonGuiBlock> void onClick(T block, ClickType clickType, Player player, InventoryClickEvent event, Runnable callback);
}

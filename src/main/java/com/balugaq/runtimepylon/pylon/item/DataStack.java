package com.balugaq.runtimepylon.pylon.item;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public interface DataStack {
    <T extends PylonBlock & PylonGuiBlock> void onClick(@NotNull T block, @NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event, @NotNull Runnable callback);
}

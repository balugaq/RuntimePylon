package com.balugaq.runtimepylon.item.fluid;

import com.balugaq.runtimepylon.item.DataStack;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.fluid.PylonFluidTag;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public interface PylonFluidTagHolder<T extends PylonFluidTag> extends DataStack {
    @NotNull T getTag();
}

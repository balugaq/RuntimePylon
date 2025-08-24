package com.balugaq.runtimepylon.pylon.item;

import com.balugaq.runtimepylon.gui.GuiItem;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.balugaq.runtimepylon.util.Lang.string_input_1;

public class StringStack extends PylonItem implements DataStack {
    public String internal;

    public StringStack(@NotNull ItemStack stack) {
        super(stack);
    }

    public boolean toBoolean() {
        return Boolean.parseBoolean(internal);
    }

    public String get() {
        return internal;
    }

    @Override
    public <T extends PylonBlock & PylonGuiBlock> void onClick(@NotNull T block, @NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event, @NotNull Runnable callback) {
        player.closeInventory();
        GuiItem.waitInput(player, string_input_1, s -> {
            internal = s;
            callback.run();
        });
    }
}

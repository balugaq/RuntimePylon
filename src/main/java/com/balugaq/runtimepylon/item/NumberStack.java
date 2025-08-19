package com.balugaq.runtimepylon.item;

import com.balugaq.runtimepylon.gui.GuiItem;
import com.balugaq.runtimepylon.util.WrongStateException;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NumberStack extends PylonItem implements DataStack{
    public String internal;
    public NumberStack(@NotNull ItemStack stack) {
        super(stack);
    }

    public int toInt() {
        try {
            return Integer.parseInt(internal);
        } catch (NumberFormatException e) {
            throw new WrongStateException("NumberStack is not a int");
        }
    }

    public double toDouble() {
        try {
            return Double.parseDouble(internal);
        } catch (NumberFormatException e) {
            throw new WrongStateException("NumberStack is not a double");
        }
    }

    public float toFloat() {
        try {
            return Float.parseFloat(internal);
        } catch (NumberFormatException e) {
            throw new WrongStateException("NumberStack is not a float");
        }
    }

    public boolean toBoolean() {
        return Boolean.parseBoolean(internal) || !internal.equals("0");
    }

    public String string() {
        return internal;
    }

    @Override
    public <T extends PylonBlock & PylonGuiBlock> void onClick(@NotNull T block, @NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        GuiItem.waitInput(player, "Enter a number", s -> {
            internal = s;
        });
    }
}

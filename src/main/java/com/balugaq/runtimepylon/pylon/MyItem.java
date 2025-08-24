package com.balugaq.runtimepylon.pylon;

import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MyItem extends PylonItem {
    @MustOverride
    public MyItem(@NotNull ItemStack stack) {
        super(stack);
    }
}

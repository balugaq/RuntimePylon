package com.balugaq.runtimepylon.object;

import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class CustomItem extends PylonItem {
    public CustomItem(final ItemStack stack) {
        super(stack);
    }
}

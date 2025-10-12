package com.balugaq.runtimepylon.util;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MaterialUtil {
    public static Material getDisplayMaterial(ItemStack itemStack) {
        Key key = itemStack.getData(DataComponentTypes.ITEM_MODEL);
        if (key != null) {
            try {
                return Material.getMaterial(key.value().toUpperCase());
            } catch (IllegalArgumentException e) {
                return itemStack.getType();
            }
        }

        return itemStack.getType();
    }
}

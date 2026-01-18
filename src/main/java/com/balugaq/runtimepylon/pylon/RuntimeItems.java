package com.balugaq.runtimepylon.pylon;

import com.balugaq.runtimepylon.pylon.page.MainPage;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class RuntimeItems {
    // @formatter:off
    public static final ItemStack recipe_copier = ItemStackBuilder.pylon(
            Material.CRAFTING_TABLE,
            RuntimeKeys.recipe_copier
    ).build();

    static {
        PylonItem.register(
                PylonItem.class,
                recipe_copier,
                RuntimeKeys.item_hub // block signature
        );
        MainPage.addItem(recipe_copier);
    }
    public static void initialize() {
    }
}

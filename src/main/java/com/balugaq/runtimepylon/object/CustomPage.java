package com.balugaq.runtimepylon.object;

import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.item.Item;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public class CustomPage extends SimpleStaticGuidePage {
    public CustomPage(final NamespacedKey key, final Material material, final List<Item> buttons) {
        super(key, material, buttons);
    }

    public CustomPage(final NamespacedKey key, final Material material) {
        super(key, material);
    }
}

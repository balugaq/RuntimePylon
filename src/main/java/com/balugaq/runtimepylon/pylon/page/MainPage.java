package com.balugaq.runtimepylon.pylon.page;

import com.balugaq.runtimepylon.pylon.RuntimeKeys;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class MainPage {
    private static final SimpleStaticGuidePage MAIN = new SimpleStaticGuidePage(RuntimeKeys.main, Material.CLOCK);

    static {
        PylonGuide.getRootPage().addPage(MAIN);
    }

    public static void addItem(ItemStack itemStack) {
        MAIN.addItem(itemStack);
    }
}

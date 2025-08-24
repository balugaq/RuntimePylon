package com.balugaq.runtimepylon.pylon.page;

import com.balugaq.runtimepylon.pylon.RuntimeKeys;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.pages.base.GuidePage;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class MainPage {
    private static final SimpleStaticGuidePage MAIN = new SimpleStaticGuidePage(RuntimeKeys.main, Material.CLOCK);

    static {
        PylonGuide.getRootPage().addPage(MAIN);
    }

    public static void addItem(@NotNull NamespacedKey key) {
        MAIN.addItem(key);
    }

    public static void addFluid(@NotNull NamespacedKey key) {
        MAIN.addFluid(key);
    }

    public static void addResearch(@NotNull NamespacedKey key) {
        MAIN.addResearch(key);
    }

    public static void addPage(@NotNull GuidePage page) {
        MAIN.addPage(page);
    }
}

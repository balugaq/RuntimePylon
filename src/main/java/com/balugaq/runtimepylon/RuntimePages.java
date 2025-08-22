package com.balugaq.runtimepylon;

import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import org.bukkit.Material;

public class RuntimePages {
    public static final SimpleStaticGuidePage MAIN = new SimpleStaticGuidePage(RuntimeKeys.main, Material.CLOCK);

    static {
        PylonGuide.getRootPage().addPage(MAIN);
    }
}

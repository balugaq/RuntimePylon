package com.balugaq.runtimepylon.pylon;

import com.balugaq.runtimepylon.pylon.block.FluidHub;
import com.balugaq.runtimepylon.pylon.block.ItemHub;
import com.balugaq.runtimepylon.pylon.block.PageHub;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import org.bukkit.Material;

public class RuntimeBlocks {
    static {
        PylonBlock.register(RuntimeKeys.item_hub, Material.PURPUR_PILLAR, ItemHub.class);
        PylonBlock.register(RuntimeKeys.fluid_hub, Material.LAPIS_BLOCK, FluidHub.class);
        PylonBlock.register(RuntimeKeys.page_hub, Material.QUARTZ_BLOCK, PageHub.class);
    }

    public static void initialize() {
    }
}

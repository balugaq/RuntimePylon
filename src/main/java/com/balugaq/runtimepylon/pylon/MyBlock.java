package com.balugaq.runtimepylon.pylon;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class MyBlock extends PylonBlock {
    protected MyBlock(@NotNull Block block) {
        super(block);
    }

    /**
     * On create new
     */
    @MustOverride
    public MyBlock(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    /**
     * On load
     */
    @MustOverride
    public MyBlock(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }
}

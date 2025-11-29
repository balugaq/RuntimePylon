package com.balugaq.runtimepylon.object;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class CustomBlock extends PylonBlock {
    public CustomBlock(final Block block) {
        super(block);
    }

    public CustomBlock(final Block block, final PersistentDataContainer pdc) {
        super(block, pdc);
    }

    public CustomBlock(final Block block, final BlockCreateContext context) {
        super(block, context);
    }
}

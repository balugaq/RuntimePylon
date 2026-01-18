package com.balugaq.pc.pylon;

import com.balugaq.pc.pylon.block.RecipeCopier;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class PylonCustomizerBlocks {
    static {
        PylonBlock.register(PylonCustomizerKeys.recipe_copier, Material.CRAFTING_TABLE, RecipeCopier.class);
    }

    public static void initialize() {
    }
}

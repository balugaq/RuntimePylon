package com.balugaq.runtimepylon.pylon;

import com.balugaq.runtimepylon.pylon.block.RecipeCopier;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class RuntimeBlocks {
    static {
        PylonBlock.register(RuntimeKeys.recipe_copier, Material.CRAFTING_TABLE, RecipeCopier.class);
    }

    public static void initialize() {
    }
}

package com.balugaq.runtimepylon.object.blocks;

import com.balugaq.runtimepylon.GlobalVars;
import com.balugaq.runtimepylon.object.CustomRecipe;
import com.balugaq.runtimepylon.object.CustomRecipeType;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

/**
 * @author balugaq
 */
@NullMarked
public class CustomMultiBlock extends CustomBlock implements PylonSimpleMultiblock {
    private final @Nullable CustomRecipeType recipeType = (CustomRecipeType) PylonRegistry.RECIPE_TYPES.get(getKey());

    public CustomMultiBlock(final Block block) {
        super(block);
    }

    public CustomMultiBlock(final Block block, final PersistentDataContainer pdc) {
        super(block, pdc);
    }

    public CustomMultiBlock(final Block block, final BlockCreateContext context) {
        super(block, context);
    }

    @Override
    public Map<Vector3i, MultiblockComponent> getComponents() {
        return GlobalVars.getMultiBlockComponents(getKey());
    }

    @Override
    public @Nullable BlockFace getFacing() {
        return PylonSimpleMultiblock.super.getFacing();
    }
}

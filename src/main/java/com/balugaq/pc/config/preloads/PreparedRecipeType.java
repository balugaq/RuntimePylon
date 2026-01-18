package com.balugaq.pc.config.preloads;

import com.balugaq.pc.config.PostLoadable;
import com.balugaq.pc.config.RegisteredObjectID;
import com.balugaq.pc.object.CustomRecipeType;
import com.balugaq.pc.object.ItemStackProvider;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;

/**
 * @author balugaq
 */
@NullMarked
public record PreparedRecipeType(
        RegisteredObjectID id,
        List<String> structure,
        @Nullable ItemStackProvider guiProvider,
        @Nullable Map<String, CustomRecipeType.Handler> configReader,
        boolean postLoad,
        @Nullable RecipeType<?> cloneType
) implements PostLoadable {
}

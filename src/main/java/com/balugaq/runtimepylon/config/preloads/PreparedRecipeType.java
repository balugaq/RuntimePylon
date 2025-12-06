package com.balugaq.runtimepylon.config.preloads;

import com.balugaq.runtimepylon.config.PostLoadable;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.object.CustomRecipeType;
import com.balugaq.runtimepylon.object.ItemStackProvider;
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
        boolean postLoad
) implements PostLoadable {
}

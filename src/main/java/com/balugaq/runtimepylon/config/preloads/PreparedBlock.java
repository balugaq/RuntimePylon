package com.balugaq.runtimepylon.config.preloads;

import com.balugaq.runtimepylon.config.PostLoadable;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.object.ItemStackProvider;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public record PreparedBlock(
        RegisteredObjectID id,
        Material material,
        boolean postLoad
) implements PostLoadable {
}

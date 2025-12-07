package com.balugaq.runtimepylon.config.preloads;

import com.balugaq.runtimepylon.config.PageDesc;
import com.balugaq.runtimepylon.config.PostLoadable;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.data.MyArrayList;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public record PreparedPage(
        RegisteredObjectID id,
        Material material,
        @Nullable MyArrayList<PageDesc> parents,
        boolean postLoad
) implements PostLoadable {
}

package com.balugaq.runtimepylon.config.preloads;

import com.balugaq.runtimepylon.config.PageDesc;
import com.balugaq.runtimepylon.config.PostLoadable;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptItemDesc;
import com.balugaq.runtimepylon.data.MyArrayList;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;

/**
 * @author balugaq
 */
@NullMarked
public record PreparedPage(
        RegisteredObjectID id,
        Material material,
        @Nullable MyArrayList<PageDesc> parents,
        List<ScriptItemDesc> items,
        boolean postLoad
) implements PostLoadable {
}

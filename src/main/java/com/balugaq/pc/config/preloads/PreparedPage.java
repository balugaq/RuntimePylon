package com.balugaq.pc.config.preloads;

import com.balugaq.pc.config.PageDesc;
import com.balugaq.pc.config.PostLoadable;
import com.balugaq.pc.config.RegisteredObjectID;
import com.balugaq.pc.config.ScriptItemDesc;
import com.balugaq.pc.data.MyArrayList;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

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

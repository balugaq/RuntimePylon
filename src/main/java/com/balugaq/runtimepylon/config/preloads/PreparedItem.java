package com.balugaq.runtimepylon.config.preloads;

import com.balugaq.runtimepylon.config.PageDesc;
import com.balugaq.runtimepylon.config.PostLoadable;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public record PreparedItem(
        RegisteredObjectID id,
        ItemStack icon,
        @Nullable List<PageDesc> pages,
        boolean postLoad
) implements PostLoadable {

}

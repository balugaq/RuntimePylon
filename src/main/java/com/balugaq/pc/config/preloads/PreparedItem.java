package com.balugaq.pc.config.preloads;

import com.balugaq.pc.config.PageDesc;
import com.balugaq.pc.config.PostLoadable;
import com.balugaq.pc.config.RegisteredObjectID;
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

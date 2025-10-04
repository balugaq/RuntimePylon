package com.balugaq.runtimepylon.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@Data
@AllArgsConstructor
@NullMarked
public class PreparedItem implements PostLoadable {
    final RegisteredObjectID id;
    final ItemStack icon;
    final @Nullable ScriptDesc script;
    final @Nullable List<InternalObjectID> pages;
    final boolean postLoad;
}

package com.balugaq.runtimepylon.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class PreparedItem {
    private final ItemStack icon;
    private final @Nullable ScriptDesc script;
    private final @Nullable List<InternalObjectID> pages;
}

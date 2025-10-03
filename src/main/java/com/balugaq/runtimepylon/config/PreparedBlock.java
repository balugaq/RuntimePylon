package com.balugaq.runtimepylon.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class PreparedBlock {
    private final Material material;
    private final @Nullable ScriptDesc script;
}

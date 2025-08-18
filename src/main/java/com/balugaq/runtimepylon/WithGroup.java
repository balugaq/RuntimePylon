package com.balugaq.runtimepylon;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

public interface WithGroup {
    @Nullable NamespacedKey getGroupId();
}

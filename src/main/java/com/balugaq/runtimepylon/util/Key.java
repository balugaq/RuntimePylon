package com.balugaq.runtimepylon.util;

import com.balugaq.runtimepylon.RuntimePylon;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class Key {
    public static NamespacedKey create(@NotNull String key) {
        return new NamespacedKey(RuntimePylon.getInstance(), key);
    }
}

package com.balugaq.runtimepylon.util;

import com.balugaq.runtimepylon.RuntimePylon;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class Keys {
    public static NamespacedKey create(String key) {
        return new NamespacedKey(RuntimePylon.getInstance(), key);
    }
}

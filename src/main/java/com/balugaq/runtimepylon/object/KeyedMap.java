package com.balugaq.runtimepylon.object;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class KeyedMap<T> extends Object2ObjectOpenHashMap<NamespacedKey, T> {
}

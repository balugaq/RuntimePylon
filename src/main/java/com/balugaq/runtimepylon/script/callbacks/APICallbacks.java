package com.balugaq.runtimepylon.script.callbacks;

import com.caoccao.javet.annotations.V8Function;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

/**
 * @author lijinhong11
 */
@NullMarked
public class APICallbacks {
    @V8Function
    public boolean containsItem(NamespacedKey key) {
        return PylonRegistry.ITEMS.contains(key);
    }

    @V8Function
    public boolean containsBlock(NamespacedKey key) {
        return PylonRegistry.BLOCKS.contains(key);
    }

    @V8Function
    public NamespacedKey createKey(String namespace, String key) {
        return new NamespacedKey(namespace, key);
    }
}

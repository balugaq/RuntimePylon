package com.balugaq.runtimepylon.script.callbacks;

import com.caoccao.javet.annotations.V8Function;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.NamespacedKey;

/**
 * @author lijinhong11
 */
public class PylonCallbackReceiver {
    @V8Function
    public boolean containsItem(NamespacedKey key) {
        return PylonRegistry.ITEMS.contains(key);
    }

    @V8Function
    public boolean containsBlock(NamespacedKey key) {
        return PylonRegistry.BLOCKS.contains(key);
    }
}

package com.balugaq.runtimepylon.script.callbacks;

import com.caoccao.javet.annotations.V8Function;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

/**
 * @author lijinhong11
 */
@NullMarked
public class RuntimePylonCallbackReceiver {
    @V8Function
    public NamespacedKey createKey(String namespace, String key) {
        return new NamespacedKey(namespace, key);
    }
}

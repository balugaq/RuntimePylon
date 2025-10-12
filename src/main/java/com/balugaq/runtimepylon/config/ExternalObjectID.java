package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.pack.PackNamespace;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public record ExternalObjectID(String id) {
    public static ExternalObjectID of(PackNamespace namespace, InternalObjectID internal) {
        return new ExternalObjectID(namespace.getNamespace() + "_" + internal.getId());
    }

    public RegisteredObjectID register() {
        return RegisteredObjectID.of(new NamespacedKey(RuntimePylon.getInstance(), id));
    }
}

package com.balugaq.runtimepylon.config;

import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record RegisteredObjectID(NamespacedKey key) {
    public static RegisteredObjectID of(NamespacedKey id) {
        return new RegisteredObjectID(id);
    }
}

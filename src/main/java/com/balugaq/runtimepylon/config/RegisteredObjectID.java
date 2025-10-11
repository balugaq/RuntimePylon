package com.balugaq.runtimepylon.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

@Data
@AllArgsConstructor
@NullMarked
public class RegisteredObjectID {
    private final NamespacedKey key;

    public static RegisteredObjectID of(NamespacedKey id) {
        return new RegisteredObjectID(id);
    }
}

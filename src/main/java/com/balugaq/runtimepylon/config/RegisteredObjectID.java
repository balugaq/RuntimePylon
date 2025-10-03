package com.balugaq.runtimepylon.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class RegisteredObjectID {
    private final NamespacedKey key;

    public static RegisteredObjectID of(NamespacedKey id) {
        return new RegisteredObjectID(id);
    }
}

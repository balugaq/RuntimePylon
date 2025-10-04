package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.pack.PackNamespace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class ExternalObjectID {
    private final String id;

    public static ExternalObjectID of(PackNamespace namespace, InternalObjectID internal) {
        return new ExternalObjectID(namespace.getNamespace() + "_" + internal.getId());
    }

    public RegisteredObjectID register() {
        return RegisteredObjectID.of(new NamespacedKey(RuntimePylon.getInstance(), id));
    }
}

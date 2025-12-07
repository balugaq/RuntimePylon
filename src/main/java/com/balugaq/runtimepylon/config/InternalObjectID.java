package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.config.pack.PackNamespace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class InternalObjectID implements Deserializer<InternalObjectID> {
    private final String id;

    public RegisteredObjectID register(PackNamespace namespace) {
        return RegisteredObjectID.of(namespace.plugin().key(id));
    }

    @Override
    public List<ConfigReader<?, InternalObjectID>> readers() {
        return ConfigReader.list(String.class, InternalObjectID::of);
    }

    public static InternalObjectID of(String id) {
        return new InternalObjectID(id);
    }
}

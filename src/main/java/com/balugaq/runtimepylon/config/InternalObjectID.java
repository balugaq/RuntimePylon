package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.config.pack.PackNamespace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class InternalObjectID implements Deserializable<InternalObjectID> {
    private final String id;

    public static InternalObjectID of(String id) {
        return new InternalObjectID(id);
    }

    public ExternalObjectID with(PackNamespace namespace) {
        return ExternalObjectID.of(namespace, this);
    }

    @Override
    public List<ConfigReader<?, InternalObjectID>> readers() {
        return List.of(
                ConfigReader.of(String.class, InternalObjectID::of)
        );
    }
}

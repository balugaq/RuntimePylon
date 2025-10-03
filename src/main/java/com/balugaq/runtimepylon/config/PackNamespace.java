package com.balugaq.runtimepylon.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class PackNamespace implements Unserializable<PackNamespace> {
    private final String id;

    @Override
    public List<Reader<?, PackNamespace>> readers() {
        return List.of(
                Reader.of(String.class, PackNamespace::new)
        );
    }
}

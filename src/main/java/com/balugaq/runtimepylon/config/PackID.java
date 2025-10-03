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
public class PackID implements Unserializable<PackID> {
    private final String id;

    @Override
    public List<Reader<?, PackID>> readers() {
        return List.of(
                Reader.of(String.class, PackID::new)
        );
    }
}

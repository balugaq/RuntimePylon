package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.Examinable;
import com.balugaq.runtimepylon.exceptions.ExamineFailedException;
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
public class PackNamespace implements Deserializer<PackNamespace>, Examinable<PackNamespace> {
    private final String namespace;

    @Override
    public PackNamespace examine() throws ExamineFailedException {
        if (!namespace.matches("[a-z0-9_\\-\\.]+")) {
            throw new ExamineFailedException("PackNamespace must be [a-z0-9_-.]+");
        }
        return this;
    }

    @Override
    public List<ConfigReader<?, PackNamespace>> readers() {
        return List.of(
                ConfigReader.of(String.class, PackNamespace::new)
        );
    }
}

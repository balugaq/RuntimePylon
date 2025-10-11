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
public class PackVersion implements Deserializer<PackVersion>, Examinable<PackVersion> {
    private final String version;

    @Override
    public PackVersion examine() throws ExamineFailedException {
        if (!version.matches("[A-Za-z0-9_\\+\\-\\./\\(\\)]+")) {
            throw new ExamineFailedException("PackVersion must be [A-Za-z0-9_+-./()]+");
        }
        return this;
    }

    @Override
    public List<ConfigReader<?, PackVersion>> readers() {
        return List.of(
                ConfigReader.of(String.class, PackVersion::new),
                ConfigReader.of(Long.class, l -> new PackVersion(l.toString())),
                ConfigReader.of(Double.class, d -> new PackVersion(d.toString()))
        );
    }
}

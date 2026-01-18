package com.balugaq.pc.config.pack;

import com.balugaq.pc.config.ConfigReader;
import com.balugaq.pc.config.Deserializer;
import com.balugaq.pc.config.Examinable;
import com.balugaq.pc.exceptions.ExamineFailedException;
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
        return ConfigReader.list(
                String.class, PackVersion::new,
                Long.class, l -> new PackVersion(l.toString()),
                Double.class, d -> new PackVersion(d.toString())
        );
    }
}

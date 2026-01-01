package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.Examinable;
import com.balugaq.runtimepylon.config.PackDesc;
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
public class PackID implements Deserializer<PackID>, Examinable<PackID> {
    private final String id;

    @Override
    public PackID examine() throws ExamineFailedException {
        if (!id.matches("[A-Za-z0-9_\\+\\-]+")) {
            throw new ExamineFailedException("Pack ID must be [A-Za-z0-9_+-]+");
        }
        return this;
    }

    @Override
    public List<ConfigReader<?, PackID>> readers() {
        return ConfigReader.list(String.class, PackID::new);
    }

    public PackDesc toDesc() {
        return new PackDesc(id);
    }
}

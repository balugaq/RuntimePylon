package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.config.pack.PackID;
import com.balugaq.runtimepylon.exceptions.ExamineFailedException;
import com.balugaq.runtimepylon.manager.PackManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class PackDesc implements Deserializer<PackDesc>, Examinable<PackDesc> {
    private final String id;

    @Override
    public PackDesc examine() throws ExamineFailedException {
        if (!id.matches("[A-Za-z0-9_+-]+")) {
            throw new ExamineFailedException("Pack Desc must be [A-Za-z0-9_+-]+");
        }
        return this;
    }

    @Override
    public List<ConfigReader<?, PackDesc>> readers() {
        return ConfigReader.list(
                String.class, PackDesc::new,
                PackID.class, p -> new PackDesc(p.getId()),
                Pack.class, p -> new PackDesc(p.getPackID().getId())
        );
    }

    @Nullable
    public Pack findPack() {
        return PackManager.findPack(this);
    }
}

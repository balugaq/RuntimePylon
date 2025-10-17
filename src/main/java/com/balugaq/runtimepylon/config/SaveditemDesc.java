package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.ExamineFailedException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.List;

/**
 * @author balugaq
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class SaveditemDesc implements Deserializer<SaveditemDesc>, Examinable<SaveditemDesc> {
    private final File file;
    private ItemStack itemStack;

    @Override
    public SaveditemDesc examine() throws ExamineFailedException {
        ItemStack itemStack = YamlConfiguration.loadConfiguration(file).getItemStack("item");
        if (itemStack == null) {
            throw new ExamineFailedException("saveditem is invalid");
        }
        this.itemStack = itemStack;
        return this;
    }

    @Override
    public List<ConfigReader<?, SaveditemDesc>> readers() {
        return List.of(
                ConfigReader.of(File.class, SaveditemDesc::new)
        );
    }

}

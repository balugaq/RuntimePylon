package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.PackManager;
import com.balugaq.runtimepylon.util.Debug;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;

/**
 * @author balugaq
 */
@Data
@AllArgsConstructor
@NullMarked
public class Researches {
    private File researchFolder;
    private PackNamespace namespace;

    public void mergeTo(File to) {
        merge(researchFolder, to);
    }

    private void merge(File from, File to) {
        if (!to.exists()) {
            to.mkdir();
        }

        File file = new File(from, "researches.yml");

        if (file.exists() && file.isFile() && file.getName().matches("researches.yml")) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            File targetFile = new File(to, file.getName());

            if (!targetFile.exists()) {
                try {
                    targetFile.createNewFile();
                    PackManager.saveConfig(config, new YamlConfiguration(), targetFile);
                } catch (IOException e) {
                    Debug.severe(e);
                }
            }
        }
    }
}

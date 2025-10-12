package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.ExternalObjectID;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.PackManager;
import com.balugaq.runtimepylon.util.Debug;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;

@Data
@AllArgsConstructor
@NullMarked
public class Settings {
    private File settingsFolder;
    private PackNamespace namespace;

    public void mergeTo(File to) {
        merge(settingsFolder, to);
    }

    private void merge(File from, File to) {
        if (!to.exists()) {
            to.mkdir();
        }

        for (File file : from.listFiles()) {
            if (file.isFile() && file.getName().matches("[a-z0-9_\\-\\./]+\\.yml$")) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                File targetFile = new File(to, ExternalObjectID.of(namespace, InternalObjectID.of(file.getName())).id());

                if (!targetFile.exists()) {
                    try {
                        targetFile.createNewFile();
                        YamlConfiguration targetConfig = YamlConfiguration.loadConfiguration(targetFile);
                        PackManager.saveConfig(config, targetConfig, targetFile);
                    } catch (IOException e) {
                        Debug.severe(e);
                    }
                }
            } else if (file.isDirectory()) {
                merge(file, new File(to, file.getName()));
            }
        }
    }
}

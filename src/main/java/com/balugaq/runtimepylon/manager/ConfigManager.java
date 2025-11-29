package com.balugaq.runtimepylon.manager;

import com.balugaq.runtimepylon.util.Debug;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author balugaq
 */
@SuppressWarnings({"ConstantValue", "unused"})
@NullMarked
public class ConfigManager {
    private final JavaPlugin plugin;
    private final boolean AUTO_UPDATE;
    private final boolean DEBUG;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setupDefaultConfig();
        FileConfiguration cfg = plugin.getConfig();

        this.AUTO_UPDATE = cfg.getBoolean("auto-update", false);
        this.DEBUG = cfg.getBoolean("debug", false);
    }

    private void setupDefaultConfig() {
        // config.yml
        final InputStream inputStream = plugin.getResource("config.yml");
        final File existingFile = new File(plugin.getDataFolder(), "config.yml");

        if (inputStream == null) {
            return;
        }

        final Reader reader = new InputStreamReader(inputStream);
        final FileConfiguration resourceConfig = YamlConfiguration.loadConfiguration(reader);
        final FileConfiguration existingConfig = YamlConfiguration.loadConfiguration(existingFile);

        for (String key : resourceConfig.getKeys(false)) {
            checkKey(existingConfig, resourceConfig, key);
        }

        try {
            existingConfig.save(existingFile);
        } catch (IOException e) {
            Debug.trace(e);
        }
    }

    private void checkKey(FileConfiguration existingConfig, FileConfiguration resourceConfig, String key) {
        final Object currentValue = existingConfig.get(key);
        final Object newValue = resourceConfig.get(key);
        if (newValue instanceof ConfigurationSection section) {
            for (String sectionKey : section.getKeys(false)) {
                checkKey(existingConfig, resourceConfig, key + "." + sectionKey);
            }
        } else if (currentValue == null) {
            existingConfig.set(key, newValue);
        }
    }

    public boolean isAutoUpdate() {
        return AUTO_UPDATE;
    }

    public boolean isDebug() {
        return DEBUG;
    }
}

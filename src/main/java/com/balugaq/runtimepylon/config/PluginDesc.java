package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.ExamineFailedException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
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
public class PluginDesc implements Deserializer<PluginDesc>, Examinable<PluginDesc> {
    private final String id;

    @Override
    public PluginDesc examine() throws ExamineFailedException {
        if (!id.matches(".+")) {
            throw new ExamineFailedException("Plugin Desc must be .+");
        }
        return this;
    }

    @Override
    public List<ConfigReader<?, PluginDesc>> readers() {
        return List.of(
                ConfigReader.of(String.class, PluginDesc::new)
        );
    }

    @Nullable
    public Plugin findPlugin() {
        return Bukkit.getPluginManager().getPlugin(id);
    }
}

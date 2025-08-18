package com.balugaq.runtimepylon;

import io.github.pylonmc.pylon.core.addon.PylonAddon;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Set;

public class RuntimePylon extends JavaPlugin implements PylonAddon {

    @Getter
    private static RuntimePylon instance;

    @Override
    public void onEnable() {
        instance = this;

        registerWithPylon();

        saveDefaultConfig();
    }

    @Override
    public @NotNull JavaPlugin getJavaPlugin() {
        return instance;
    }

    @Override
    public @NotNull Set<@NotNull Locale> getLanguages() {
        return Set.of(
                Locale.ENGLISH,
                Locale.SIMPLIFIED_CHINESE
        );
    }

    @Override
    public @NotNull Material getMaterial() {
        return Material.COPPER_INGOT;
    }
}

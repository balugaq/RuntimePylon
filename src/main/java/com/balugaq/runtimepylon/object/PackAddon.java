package com.balugaq.runtimepylon.object;

import com.balugaq.runtimepylon.RuntimePylon;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.Set;

/**
 * @author balugaq
 */
@NullMarked
public record PackAddon(String namespace, Set<Locale> languages, Material material) implements PylonAddon {
    @Override
    public JavaPlugin getJavaPlugin() {
        return RuntimePylon.getInstance();
    }

    @Override
    public Set<Locale> getLanguages() {
        return languages;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public NamespacedKey getKey() {
        return key(namespace);
    }

    public NamespacedKey key(String key) {
        return new NamespacedKey(namespace, key);
    }
}

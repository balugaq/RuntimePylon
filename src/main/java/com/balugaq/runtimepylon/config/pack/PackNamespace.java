package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.object.PackAddon;
import com.balugaq.runtimepylon.PackAddonGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.Set;

/**
 * @author balugaq
 */
@Data
@NoArgsConstructor(force = true)
@NullMarked
public class PackNamespace {
    private final String namespace;
    private final Set<Locale> languages;
    private final Material material;
    private PackAddon plugin;

    public PackNamespace(String namespace, Set<Locale> languages, Material material) {
        this.namespace = namespace;
        this.languages = languages;
        this.material = material;
        plugin = PackAddonGenerator.generate(namespace, languages, material);
    }

    public static PackNamespace warp(PackID packID, Set<Locale> languages, Material material) {
        return new PackNamespace(packID.getId().toLowerCase(), languages, material);
    }

    public PackAddon plugin() {
        return plugin;
    }
}

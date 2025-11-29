package com.balugaq.runtimepylon;

import com.balugaq.runtimepylon.object.PackAddon;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.Set;

/**
 * @author balugaq
 */
@NullMarked
public class PackAddonGenerator {
    public static PackAddon generate(String id, Set<Locale> languages, Material material) {
        return new PackAddon(id, languages, material);
    }
}

package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.GlobalVars;
import com.balugaq.runtimepylon.PackAddonGenerator;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.object.PackAddon;
import com.balugaq.runtimepylon.script.ScriptExecutor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
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
    private @Nullable Scripts scripts;
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

    public void registerScript(RegisteredObjectID id, @Nullable ScriptDesc desc) {
        var exe = findScript(desc);
        if (exe == null) return;
        GlobalVars.putScript(id.key(), exe);
    }

    @Nullable
    public ScriptExecutor findScript(@Nullable ScriptDesc desc) {
        if (scripts == null || desc == null) return null;
        return scripts.findScript(desc);
    }
}

package com.balugaq.pc.config.pack;

import com.balugaq.pc.GlobalVars;
import com.balugaq.pc.config.RegisteredObjectID;
import com.balugaq.pc.config.ScriptDesc;
import com.balugaq.pc.object.PackAddon;
import com.balugaq.pc.script.ScriptExecutor;
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
        plugin = PackAddon.generate(namespace, languages, material);
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

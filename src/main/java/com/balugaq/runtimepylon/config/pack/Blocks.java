package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializable;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.PreparedBlock;
import com.balugaq.runtimepylon.config.PreparedItem;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.util.Debug;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <li>blocks/
 *   <ul>
 *     <li>blocks-partA.yml</li>
 *     <li>blocks-partB.yml</li>
 *   </ul>
 * </li>
 *
 * For each yml:
 * <p>
 * [Internal object ID]:
 *   material: [Material Format]
 *   *script: script.js
 * <p>
 *
 */
@Data
@NullMarked
public class Blocks implements FileObject<Blocks> {
    private PackNamespace namespace;
    private Map<RegisteredObjectID, PreparedBlock> blocks;

    public Blocks setPackNamespace(PackNamespace namespace) {
        this.namespace = namespace;
        return this;
    }

    @Override
    public List<FileReader<Blocks>> readers() {
        return List.of(
                dir -> {
                    List<File> files = Arrays.stream(dir.listFiles()).toList();
                    List<File> ymls = files.stream().filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")).toList();
                    for (File yml : ymls) {
                        var config = YamlConfiguration.loadConfiguration(yml);

                        for (String blockKey : config.getKeys(false)) {
                            if (!blockKey.matches("a-z0-9_-\\./")) {
                                Debug.severe("Incompatible block key: " + blockKey);
                                continue;
                            }

                            ConfigurationSection section = config.getConfigurationSection(blockKey);
                            if (section == null) {
                                Debug.severe("Invalid block desc at " + blockKey);
                                continue;
                            }

                            if (!section.contains("material")) {
                                Debug.severe("No material key found at " + blockKey);
                                continue;
                            }

                            var s2 = section.getConfigurationSection("material");
                            if (s2 == null) {
                                Debug.severe("Invalid material section at " + blockKey);
                                continue;
                            }

                            ItemStack item = Deserializable.ITEMSTACK.deserialize(s2);
                            var id = InternalObjectID.of(blockKey).with(namespace).register();

                            ScriptDesc scriptdesc = Deserializable.newDeserializer(ScriptDesc.class).deserialize(section.getString("script"));

                            blocks.put(id, new PreparedBlock(item.getType(), scriptdesc));
                        }
                    }

                    return this;
                }
        );
    }
}

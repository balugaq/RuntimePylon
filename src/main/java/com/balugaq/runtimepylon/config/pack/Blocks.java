package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.PreparedBlock;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.exceptions.IncompatibleKeyFormatException;
import com.balugaq.runtimepylon.exceptions.InvalidDescException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
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
 * <p>
 * For each yml:
 * <p>
 * [Internal object ID]:
 *   material: [Material Format]
 *   *script: script.js
 *   *postload: boolean
 * <p>
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
                                severe(new IncompatibleKeyFormatException(blockKey));
                                continue;
                            }

                            ConfigurationSection section = config.getConfigurationSection(blockKey);
                            if (section == null) {
                                severe(new InvalidDescException(blockKey));
                                continue;
                            }

                            if (!section.contains("material")) {
                                severe(new MissingArgumentException("material"));
                                continue;
                            }

                            var s2 = section.get("material");

                            ItemStack item = Deserializer.ITEMSTACK.deserialize(s2);
                            var id = InternalObjectID.of(blockKey).with(namespace).register();

                            ScriptDesc scriptdesc = Deserializer.newDeserializer(ScriptDesc.class).deserialize(section.getString("script"));

                            boolean postLoad = section.getBoolean("postload", false);
                            blocks.put(id, new PreparedBlock(id, item.getType(), scriptdesc, postLoad));
                        }
                    }

                    return this;
                }
        );
    }
}

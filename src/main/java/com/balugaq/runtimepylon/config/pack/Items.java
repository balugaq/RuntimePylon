package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PreparedItem;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.config.UnsArrayList;
import com.balugaq.runtimepylon.util.Debug;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
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
 * <li>items/
 *   <ul>
 *     <li>items-partA.yml</li>
 *     <li>items-partB.yml</li>
 *   </ul>
 * </li>
 * <p>
 * For each yml:
 * <p>
 * [Internal object ID]:
 *   icon: [Material Format]
 *   *script: script.js
 *   *pages:
 *   - [Internal object ID]
 *   *postload: boolean
 * <p>
 */
@Data
@NullMarked
public class Items implements FileObject<Items> {
    private PackNamespace namespace;
    private Map<RegisteredObjectID, PreparedItem> items;

    public Items setPackNamespace(PackNamespace namespace) {
        this.namespace = namespace;
        return this;
    }

    @Override
    public List<FileReader<Items>> readers() {
        return List.of(
                dir -> {
                    List<File> files = Arrays.stream(dir.listFiles()).toList();
                    List<File> ymls = files.stream().filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")).toList();
                    for (File yml : ymls) {
                        var config = YamlConfiguration.loadConfiguration(yml);

                        for (String itemKey : config.getKeys(false)) {
                            if (!itemKey.matches("a-z0-9_-\\./")) {
                                Debug.severe("Incompatible item key: " + itemKey);
                                continue;
                            }

                            ConfigurationSection section = config.getConfigurationSection(itemKey);
                            if (section == null) {
                                Debug.severe("Invalid item desc at " + itemKey);
                                continue;
                            }

                            if (!section.contains("icon")) {
                                Debug.severe("No icon key found at " + itemKey);
                                continue;
                            }

                            var s2 = section.getConfigurationSection("icon");
                            if (s2 == null) {
                                Debug.severe("Invalid icon section at " + itemKey);
                                continue;
                            }

                            ItemStack item = Deserializer.ITEMSTACK.deserialize(s2);

                            var id = InternalObjectID.of(itemKey).with(namespace).register();
                            ItemStack icon = ItemStackBuilder.pylonItem(item.getType(), id.getKey()).amount(item.getAmount()).build();

                            ScriptDesc scriptdesc = Deserializer.newDeserializer(ScriptDesc.class).deserialize(section.getString("script"));
                            UnsArrayList<InternalObjectID> pages = Pack.readGenericOrNull(section, UnsArrayList.class, InternalObjectID.class, "pages");

                            boolean postLoad = section.getBoolean("postload", false);
                            items.put(id, new PreparedItem(id, icon, scriptdesc, pages, postLoad));
                        }
                    }

                    return this;
                }
        );
    }
}

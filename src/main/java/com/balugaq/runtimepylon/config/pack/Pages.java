package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.PreparedPage;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.exceptions.IncompatibleKeyFormatException;
import com.balugaq.runtimepylon.exceptions.IncompatibleMaterialException;
import com.balugaq.runtimepylon.exceptions.InvalidDescException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <li>pages/
 *   <ul>
 *     <li>pages-partA.yml</li>
 *     <li>pages-partB.yml</li>
 *   </ul>
 * </li>
 * <p>
 * For each yml:
 * <p>
 * [Internal object ID]:
 *   material: [Material Format]
 *   *postload: boolean
 * <p>
 */
@Data
@NullMarked
public class Pages implements FileObject<Pages> {
    private PackNamespace namespace;
    private Map<RegisteredObjectID, PreparedPage> pages = new HashMap<>();

    public Pages setPackNamespace(PackNamespace namespace) {
        this.namespace = namespace;
        return this;
    }

    @Override
    public List<FileReader<Pages>> readers() {
        return List.of(
                dir -> {
                    List<File> files = Arrays.stream(dir.listFiles()).toList();
                    List<File> ymls = files.stream().filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")).toList();
                    for (File yml : ymls) {
                        var config = YamlConfiguration.loadConfiguration(yml);

                        for (String pageKey : config.getKeys(false)) {
                            if (!pageKey.matches("[a-z0-9_\\-\\./]+")) {
                                severe(new IncompatibleKeyFormatException(pageKey));
                                continue;
                            }

                            ConfigurationSection section = config.getConfigurationSection(pageKey);
                            if (section == null) {
                                severe(new InvalidDescException(pageKey));
                                continue;
                            }

                            if (!section.contains("material")) {
                                severe(new MissingArgumentException("material"));
                                continue;
                            }

                            var s2 = section.get("material");
                            ItemStack item = Deserializer.ITEMSTACK.deserialize(s2);
                            if (item == null) continue;
                            if (!item.getType().isItem() || item.getType().isAir()) {
                                severe(new IncompatibleMaterialException("material must be items: " + item.getType()));
                                continue;
                            }
                            var id = InternalObjectID.of(pageKey).with(namespace).register();

                            boolean postLoad = section.getBoolean("postload", false);
                            pages.put(id, new PreparedPage(id, item.getType(), postLoad));
                        }
                    }

                    return this;
                }
        );
    }
}

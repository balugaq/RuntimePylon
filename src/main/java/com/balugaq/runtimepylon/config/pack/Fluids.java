package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializable;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.PreparedFluid;
import com.balugaq.runtimepylon.config.PreparedItem;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.util.Debug;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <li>fluids/
 *   <ul>
 *     <li>fluids-partA.yml</li>
 *     <li>fluids-partB.yml</li>
 *   </ul>
 * </li>
 *
 * For each yml:
 * <p>
 * [Internal object ID]:
 *   material: [Material Format]
 *   *temperature: [Temperature]
 * <p>
 *
 */
@Data
@NullMarked
public class Fluids implements FileObject<Fluids> {
    private PackNamespace namespace;
    private Map<RegisteredObjectID, PreparedFluid> fluids;

    public Fluids setPackNamespace(PackNamespace namespace) {
        this.namespace = namespace;
        return this;
    }

    @Override
    public List<FileReader<Fluids>> readers() {
        return List.of(
                dir -> {
                    List<File> files = Arrays.stream(dir.listFiles()).toList();
                    List<File> ymls = files.stream().filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")).toList();
                    for (File yml : ymls) {
                        var config = YamlConfiguration.loadConfiguration(yml);

                        for (String fluidKey : config.getKeys(false)) {
                            if (!fluidKey.matches("a-z0-9_-\\./")) {
                                Debug.severe("Incompatible fluid key: " + fluidKey);
                                continue;
                            }

                            ConfigurationSection section = config.getConfigurationSection(fluidKey);
                            if (section == null) {
                                Debug.severe("Invalid fluid desc at " + fluidKey);
                                continue;
                            }

                            if (!section.contains("material")) {
                                Debug.severe("No material key found at " + fluidKey);
                                continue;
                            }

                            var s2 = section.getConfigurationSection("material");
                            if (s2 == null) {
                                Debug.severe("Invalid material section at " + fluidKey);
                                continue;
                            }

                            ItemStack item = Deserializable.ITEMSTACK.deserialize(s2);

                            var id = InternalObjectID.of(fluidKey).with(namespace).register();
                            var ts = section.getString("temperature");
                            if (ts == null) {
                                Debug.severe("No temperature key found at " + fluidKey);
                                continue;
                            }

                            FluidTemperature temperature = Deserializable.enumDeserializer(FluidTemperature.class)
                                            .deserialize(ts);

                            fluids.put(id, new PreparedFluid(item.getType(), temperature));
                        }
                    }

                    return this;
                }
        );
    }
}

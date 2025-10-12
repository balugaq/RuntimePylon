package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PageDesc;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.StackWalker;
import com.balugaq.runtimepylon.config.UnsArrayList;
import com.balugaq.runtimepylon.config.preloads.PreparedFluid;
import com.balugaq.runtimepylon.exceptions.IncompatibleKeyFormatException;
import com.balugaq.runtimepylon.exceptions.IncompatibleMaterialException;
import com.balugaq.runtimepylon.exceptions.InvalidDescException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.util.MaterialUtil;
import com.balugaq.runtimepylon.util.StringUtil;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
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
 * <li>fluids/
 *   <ul>
 *     <li>fluids-partA.yml</li>
 *     <li>fluids-partB.yml</li>
 *   </ul>
 * </li>
 * <p>
 * For each yml:
 * <p>
 * [Internal object ID]:
 *   material: [Material Format]
 *   *temperature: [Temperature]
 *   *postload: boolean
 * <p>
 */
@Data
@NullMarked
public class Fluids implements FileObject<Fluids> {
    private PackNamespace namespace;
    private Map<RegisteredObjectID, PreparedFluid> fluids = new HashMap<>();

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
                        try (var ignored = StackWalker.setPosition("Reading file: " + StringUtil.simplifyPath(yml.getAbsolutePath()))) {
                            var config = YamlConfiguration.loadConfiguration(yml);

                            for (String fluidKey : config.getKeys(false)) {
                                try (var ignored1 = StackWalker.setPosition("Reading key: " + fluidKey)) {
                                    if (!fluidKey.matches("[a-z0-9_\\-\\./]+")) {
                                        throw new IncompatibleKeyFormatException(fluidKey);
                                    }

                                    ConfigurationSection section = config.getConfigurationSection(fluidKey);
                                    if (section == null) {
                                        throw new InvalidDescException(fluidKey);
                                    }

                                    if (!section.contains("material")) {
                                        throw new MissingArgumentException("material");
                                    }

                                    var s2 = section.get("material");

                                    ItemStack item = Deserializer.ITEMSTACK.deserialize(s2);
                                    if (item == null) continue;
                                    if (!item.getType().isItem() || item.getType().isAir()) {
                                        throw new IncompatibleMaterialException("material must be items: " + item.getType());
                                    }

                                    var id = InternalObjectID.of(fluidKey).with(namespace).register();
                                    var ts = section.getString("temperature");
                                    if (ts == null) {
                                        ts = FluidTemperature.NORMAL.name();
                                    }

                                    FluidTemperature temperature = Deserializer.enumDeserializer(FluidTemperature.class)
                                            .deserialize(ts.toUpperCase());
                                    if (temperature == null) continue;

                                    PageDesc page = Pack.readOrNull(section, PageDesc.class, "page", t -> t.setPackNamespace(getNamespace()));
                                    UnsArrayList<PageDesc> pages = Pack.readOrNull(section, UnsArrayList.class, PageDesc.class, "pages", t -> t.setPackNamespace(getNamespace()));
                                    if (page != null) {
                                        if (pages == null) pages = new UnsArrayList<>();
                                        pages.add(page);
                                    }

                                    boolean postLoad = section.getBoolean("postload", false);
                                    fluids.put(id, new PreparedFluid(id, MaterialUtil.getDisplayMaterial(item), temperature, pages, postLoad));
                                } catch (Exception e) {
                                    StackWalker.handle(e);
                                }
                            }
                        } catch (Exception e) {
                            StackWalker.handle(e);
                        }
                    }

                    return this;
                }
        );
    }
}

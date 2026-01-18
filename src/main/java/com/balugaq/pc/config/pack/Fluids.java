package com.balugaq.pc.config.pack;

import com.balugaq.pc.config.Deserializer;
import com.balugaq.pc.config.FileObject;
import com.balugaq.pc.config.FileReader;
import com.balugaq.pc.config.InternalObjectID;
import com.balugaq.pc.config.Pack;
import com.balugaq.pc.config.PageDesc;
import com.balugaq.pc.config.RegisteredObjectID;
import com.balugaq.pc.config.StackFormatter;
import com.balugaq.pc.config.preloads.PreparedFluid;
import com.balugaq.pc.config.register.PreRegister;
import com.balugaq.pc.data.MyArrayList;
import com.balugaq.pc.exceptions.IncompatibleMaterialException;
import com.balugaq.pc.exceptions.MissingArgumentException;
import com.balugaq.pc.util.MaterialUtil;
import com.balugaq.pc.util.StringUtil;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
 *
 * @author balugaq
 */
@Data
@NullMarked
public class Fluids implements FileObject<Fluids> {
    private AtomicInteger loadedFluids = new AtomicInteger(0);
    private PackNamespace namespace;
    private Map<RegisteredObjectID, PreparedFluid> fluids = new HashMap<>();

    public Fluids setPackNamespace(PackNamespace namespace) {
        this.namespace = namespace;
        return this;
    }

    // @formatter:off
    @Override
    public List<FileReader<Fluids>> readers() {
        return List.of(dir -> {
            List<File> files = Arrays.stream(dir.listFiles()).toList();
            List<File> ymls = files.stream().filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")).toList();
            for (File yml : ymls) {try (var ignored = StackFormatter.setPosition("Reading file: " + StringUtil.simplifyPath(yml.getAbsolutePath()))) {
                var config = YamlConfiguration.loadConfiguration(yml);
                for (String key : config.getKeys(false)) {try (var ignored1 = StackFormatter.setPosition("Reading key: " + key)) {
                    var section = PreRegister.read(config, key);
                    if (section == null) continue;

                    if (!section.contains("material")) throw new MissingArgumentException("material");

                    var s2 = section.get("material");

                    ItemStack item = Deserializer.ITEMSTACK.deserialize(s2);
                    if (item == null) continue;
                    Material dm = MaterialUtil.getDisplayMaterial(item);
                    if (!dm.isItem() || dm.isAir()) throw new IncompatibleMaterialException("material must be items: " + item.getType());

                    var id = InternalObjectID.of(key).register(namespace);
                    var ts = section.getString("temperature");
                    if (ts == null) {
                        ts = FluidTemperature.NORMAL.name();
                    }

                    FluidTemperature temperature = Deserializer.FLUID_TEMPERATURE.deserialize(ts);
                    if (temperature == null) continue;

                    PageDesc page = Pack.readOrNull(section, PageDesc.class, "page", t -> t.setPackNamespace(getNamespace()));
                    MyArrayList<PageDesc> pages = Pack.readOrNull(section, MyArrayList.class, PageDesc.class, "pages", t -> t.setPackNamespace(getNamespace()));
                    if (page != null) {
                        if (pages == null) pages = new MyArrayList<>();
                        pages.add(page);
                    }

                    boolean postLoad = section.getBoolean("postload", false);
                    fluids.put(id, new PreparedFluid(id, dm, temperature, pages, postLoad));
                } catch (Exception e) {
                    StackFormatter.handle(e);
                }}
            } catch (Exception e) {
                StackFormatter.handle(e);
            }}

            return this;
        });
    }
    // @formatter:on
}

package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.StackFormatter;
import com.balugaq.runtimepylon.config.preloads.PreparedResearch;
import com.balugaq.runtimepylon.config.register.PreRegister;
import com.balugaq.runtimepylon.exceptions.IncompatibleMaterialException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.util.MaterialUtil;
import com.balugaq.runtimepylon.util.StringUtil;
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

/**
 * <li>researches/
 *   <ul>
 *     <li>researches-partA.yml</li>
 *     <li>researches-partB.yml</li>
 *   </ul>
 * </li>
 * <p>
 * For each yml:
 * <p>
 * [Internal object ID]:
 *   material: [Material]
 *   cost: [Integer]
 *   unlocks:
 *     - [Pylon Item ID]
 *   *name: [String]
 *   *postload: boolean
 * <p>
 *
 * @author balugaq
 */
@Data
@NullMarked
public class Researches implements FileObject<Researches> {
    private PackNamespace namespace;
    private Map<RegisteredObjectID, PreparedResearch> researches = new HashMap<>();

    public Researches setPackNamespace(PackNamespace namespace) {
        this.namespace = namespace;
        return this;
    }

    // @formatter:off
    @Override
    public List<FileReader<Researches>> readers() {
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

                    String name = section.getString("name");

                    long cost = section.getLong("cost");
                    if (cost <= 0) throw new IllegalArgumentException("cost must be positive: " + cost);

                    var id = InternalObjectID.of(key).register(namespace);

                    List<String> unlocks = section.getStringList("unlocks");
                    boolean postLoad = section.getBoolean("postload", false);
                    researches.put(id, new PreparedResearch(id, dm, name, cost, unlocks, postLoad));
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

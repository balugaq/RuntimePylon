package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PreRegister;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.config.StackWalker;
import com.balugaq.runtimepylon.config.preloads.PreparedBlock;
import com.balugaq.runtimepylon.exceptions.IncompatibleKeyFormatException;
import com.balugaq.runtimepylon.exceptions.IncompatibleMaterialException;
import com.balugaq.runtimepylon.exceptions.InvalidDescException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.util.MaterialUtil;
import com.balugaq.runtimepylon.util.StringUtil;
import lombok.Data;
import org.bukkit.Material;
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
 * @author balugaq
 */
@Data
@NullMarked
public class Blocks implements FileObject<Blocks> {
    private PackNamespace namespace;
    private Map<RegisteredObjectID, PreparedBlock> blocks = new HashMap<>();

    public Blocks setPackNamespace(PackNamespace namespace) {
        this.namespace = namespace;
        return this;
    }

    // @formatter:off
    @Override
    public List<FileReader<Blocks>> readers() {
        return List.of(
                dir -> {
                    List<File> files = Arrays.stream(dir.listFiles()).toList();
                    List<File> ymls = files.stream().filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")).toList();

                    for (File yml : ymls) {try (var ignored = StackWalker.setPosition("Reading file: " + StringUtil.simplifyPath(yml.getAbsolutePath()))) {
                        var config = YamlConfiguration.loadConfiguration(yml);

                        for (String blockKey : config.getKeys(false)) {try (var ignored1 = StackWalker.setPosition("Reading key: " + blockKey)) {
                            if (!blockKey.matches("[a-z0-9_\\-\\./]+")) throw new IncompatibleKeyFormatException(blockKey);

                            ConfigurationSection section = config.getConfigurationSection(blockKey);
                            if (section == null) throw new InvalidDescException(blockKey);
                            if (PreRegister.blocks(section)) continue;

                            if (!section.contains("material")) throw new MissingArgumentException("material");

                            var s2 = section.get("material");

                            ItemStack item = Deserializer.ITEMSTACK.deserialize(s2);
                            if (item == null) continue;
                            Material dm = MaterialUtil.getDisplayMaterial(item);
                            if (!dm.isBlock() || dm.isAir()) throw new IncompatibleMaterialException("material must be blocks: " + item.getType());

                            var id = InternalObjectID.of(blockKey).with(namespace).register();

                            ScriptDesc scriptdesc = Pack.readOrNull(section, ScriptDesc.class, "script");

                            boolean postLoad = section.getBoolean("postload", false);
                            blocks.put(id, new PreparedBlock(id, dm, scriptdesc, postLoad));
                        } catch (Exception e) {
                            StackWalker.handle(e);
                        }}
                    } catch (Exception e) {
                        StackWalker.handle(e);
                    }}

                    return this;
                }
        );
    }
    // @formatter:off
}

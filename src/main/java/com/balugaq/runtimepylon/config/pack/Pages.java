package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.MyArrayList;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PageDesc;
import com.balugaq.runtimepylon.config.PreRegister;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.config.StackFormatter;
import com.balugaq.runtimepylon.config.preloads.PreparedPage;
import com.balugaq.runtimepylon.exceptions.IncompatibleMaterialException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.util.MaterialUtil;
import com.balugaq.runtimepylon.util.StringUtil;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
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
 *
 * @author balugaq
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

    // @formatter:off
    @SuppressWarnings("unchecked")@Override
    public List<FileReader<Pages>> readers() {
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

                    ScriptDesc scriptdesc = Pack.readOrNull(section, ScriptDesc.class, "script");
                    namespace.registerScript(id, scriptdesc);
                    MyArrayList<PageDesc> parents = (MyArrayList<@NotNull PageDesc>) Pack.readOrNull(section, MyArrayList.class, PageDesc.class, "parents", e -> e.setPackNamespace(namespace));

                    boolean postLoad = section.getBoolean("postload", false);
                    pages.put(id, new PreparedPage(id, dm, parents, postLoad));
                } catch (Exception e) {
                    StackFormatter.handle(e);
                }}
            } catch (Exception e) {
                StackFormatter.handle(e);
            }}

            return this;
        });
    }
    // @formatter:off
}

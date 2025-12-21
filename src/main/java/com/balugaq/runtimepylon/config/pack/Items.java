package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PageDesc;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.config.StackFormatter;
import com.balugaq.runtimepylon.config.preloads.PreparedItem;
import com.balugaq.runtimepylon.config.register.PreRegister;
import com.balugaq.runtimepylon.data.MyArrayList;
import com.balugaq.runtimepylon.exceptions.IncompatibleMaterialException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.util.MaterialUtil;
import com.balugaq.runtimepylon.util.StringUtil;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
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
 *   *script: [ScriptDesc]
 *   *page: [PageDesc]
 *   *pages:
 *   - [PageDesc]
 *   *postload: boolean
 *   *equipment-type: [Key]
 * <p>
 * [Key]: [NamespacedKey]
 * <p>
 *
 * @author balugaq
 */
@Data
@NullMarked
public class Items implements FileObject<Items> {
    private AtomicInteger loadedItems = new AtomicInteger(0);
    private PackNamespace namespace;
    private Map<RegisteredObjectID, PreparedItem> items = new HashMap<>();

    public Items setPackNamespace(PackNamespace namespace) {
        this.namespace = namespace;
        return this;
    }

    // @formatter:off
    @Override
    public List<FileReader<Items>> readers() {
        return List.of(dir -> {
            List<File> files = Arrays.stream(dir.listFiles()).toList();
            List<File> ymls = files.stream().filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")).toList();
            for (File yml : ymls) {try (var ignored = StackFormatter.setPosition("Reading file: " + StringUtil.simplifyPath(yml.getAbsolutePath()))) {
                var config = YamlConfiguration.loadConfiguration(yml);

                for (String key : config.getKeys(false)) {try (var ignored1 = StackFormatter.setPosition("Reading key: " + key)) {
                    var section = PreRegister.read(config, key);
                    if (section == null) continue;

                    if (!section.contains("icon")) throw new MissingArgumentException("icon");

                    var s2 = section.get("icon");
                    ItemStack item = Deserializer.ITEMSTACK.deserialize(s2);
                    if (item == null) continue;
                    Material dm = MaterialUtil.getDisplayMaterial(item);
                    if (!dm.isItem() || dm.isAir()) throw new IncompatibleMaterialException("material must be items: " + item.getType());

                    var id = InternalObjectID.of(key).register(namespace);
                    ItemStack icon = ItemStackBuilder.pylon(dm, id.key()).amount(item.getAmount()).build();

                    ScriptDesc scriptdesc = Pack.readOrNull(section, ScriptDesc.class, "script");
                    namespace.registerScript(id, scriptdesc);

                    PageDesc page = Pack.readOrNull(section, PageDesc.class, "page", t -> t.setPackNamespace(getNamespace()));
                    MyArrayList<PageDesc> pages = Pack.readOrNull(section, MyArrayList.class, PageDesc.class, "pages", t -> t.setPackNamespace(getNamespace()));
                    if (page != null) {
                        if (pages == null) pages = new MyArrayList<>();
                        pages.add(page);
                    }

                    boolean postLoad = section.getBoolean("postload", false);
                    items.put(id, new PreparedItem(id, icon, pages, postLoad));
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

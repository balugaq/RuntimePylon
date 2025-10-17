package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PageDesc;
import com.balugaq.runtimepylon.config.PreRegister;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.config.StackWalker;
import com.balugaq.runtimepylon.config.UnsArrayList;
import com.balugaq.runtimepylon.config.preloads.PreparedItem;
import com.balugaq.runtimepylon.exceptions.IncompatibleKeyFormatException;
import com.balugaq.runtimepylon.exceptions.IncompatibleMaterialException;
import com.balugaq.runtimepylon.exceptions.InvalidDescException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.util.StringUtil;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
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
 *   *page: [PageDesc]
 *   *pages:
 *   - [PageDesc]
 *   *postload: boolean
 * <p>
 * @author balugaq
 */
@Data
@NullMarked
public class Items implements FileObject<Items> {
    private PackNamespace namespace;
    private Map<RegisteredObjectID, PreparedItem> items = new HashMap<>();

    public Items setPackNamespace(PackNamespace namespace) {
        this.namespace = namespace;
        return this;
    }

    // @formatter:off
    @Override
    public List<FileReader<Items>> readers() {
        return List.of(
                dir -> {
                    List<File> files = Arrays.stream(dir.listFiles()).toList();
                    List<File> ymls = files.stream().filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")).toList();
                    for (File yml : ymls) {try (var ignored = StackWalker.setPosition("Reading file: " + StringUtil.simplifyPath(yml.getAbsolutePath()))) {
                        var config = YamlConfiguration.loadConfiguration(yml);

                        for (String itemKey : config.getKeys(false)) {try (var ignored1 = StackWalker.setPosition("Reading key: " + itemKey)) {
                            if (!itemKey.matches("[a-z0-9_\\-\\./]+")) throw new IncompatibleKeyFormatException(itemKey);

                            ConfigurationSection section = config.getConfigurationSection(itemKey);
                            if (section == null) throw new InvalidDescException(itemKey);
                            if (PreRegister.blocks(section)) continue;

                            if (!section.contains("icon")) throw new MissingArgumentException("icon");

                            var s2 = section.get("icon");
                            ItemStack item = Deserializer.ITEMSTACK.deserialize(s2);
                            if (item == null) continue;
                            if (!item.getType().isItem() || item.getType().isAir()) throw new IncompatibleMaterialException("material must be items: " + item.getType());

                            var id = InternalObjectID.of(itemKey).with(namespace).register();
                            ItemStack icon = ItemStackBuilder.pylonItem(item.getType(), id.key()).amount(item.getAmount()).build();

                            ScriptDesc scriptdesc = Pack.readOrNull(section, ScriptDesc.class, "script");
                            PageDesc page = Pack.readOrNull(section, PageDesc.class, "page", t -> t.setPackNamespace(getNamespace()));
                            UnsArrayList<PageDesc> pages = Pack.readOrNull(section, UnsArrayList.class, PageDesc.class, "pages", t -> t.setPackNamespace(getNamespace()));
                            if (page != null) {
                                if (pages == null) pages = new UnsArrayList<>();
                                pages.add(page);
                            }

                            boolean postLoad = section.getBoolean("postload", false);
                            items.put(id, new PreparedItem(id, icon, scriptdesc, pages, postLoad));
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
    // @formatter:on
}

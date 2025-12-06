package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.GuiReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PreRegister;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.config.StackFormatter;
import com.balugaq.runtimepylon.config.preloads.PreparedRecipeType;
import com.balugaq.runtimepylon.exceptions.IncompatibleKeyFormatException;
import com.balugaq.runtimepylon.exceptions.InvalidDescException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.object.CustomRecipeType;
import com.balugaq.runtimepylon.util.StringUtil;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.item.Item;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [Internal Object ID]:
 *   structure: |-
 *     B B B B B B B B B
 *     I a b I B O 1 2 O
 *     I c d I B O 3 4 O
 *     I e f I B O 5 6 O
 *     B B B B B B B B B
 *   *script: [ScriptDesc]
 *   *postload: boolean
 *   *gui:
 *     [char]: [Material Format]
 *   *loader:
 *     [key]: [Adapter Desc]
 * [Adapter Desc]:
 * int;3
 * list.int
 * map.map.int
 *
 * @author balugaq
 */
@Data
@NullMarked
public class RecipeTypes implements FileObject<RecipeTypes> {
    private PackNamespace namespace;
    private Map<RegisteredObjectID, PreparedRecipeType> recipeTypes = new HashMap<>();

    public RecipeTypes setPackNamespace(PackNamespace namespace) {
        this.namespace = namespace;
        return this;
    }

    @Override
    public List<FileReader<RecipeTypes>> readers() {
        return List.of(dir -> {
            List<File> files = Arrays.stream(dir.listFiles()).toList();
            List<File> ymls = files.stream().filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")).toList();

            for (File yml : ymls) {try (var ignored = StackFormatter.setPosition("Reading file: " + StringUtil.simplifyPath(yml.getAbsolutePath()))) {
                var config = YamlConfiguration.loadConfiguration(yml);

                for (String key : config.getKeys(false)) {try (var ignored1 = StackFormatter.setPosition("Reading key: " + key)) {
                    var section = PreRegister.read(config, key);
                    if (section == null) continue;

                    Map<String, CustomRecipeType.Handler> loader = null;
                    if (section.contains("loader")) {
                        ConfigurationSection sec = section.getConfigurationSection("loader");
                        if (sec != null) {
                            for (String k : sec.getKeys(false)) {
                                var s2 = sec.getString(k);
                                if (s2 != null) {
                                    if (loader == null) loader = new HashMap<>();
                                    loader.put(k, Deserializer.newDeserializer(CustomRecipeType.Handler.class).deserialize(s2));
                                }
                            }
                            /*
                            ```yml
                            loader:
                              inputs: int
                              results: int;3
                            ```
                             */
                        }
                    }
                    var id = InternalObjectID.of(key).register(namespace);
                    ScriptDesc scriptdesc = Pack.readOrNull(section, ScriptDesc.class, "script");
                    namespace.registerScript(id, scriptdesc);

                    if (!section.contains("structure")) throw new MissingArgumentException("structure");
                    var gui = GuiReader.read(section, namespace, scriptdesc);

                    boolean postLoad = section.getBoolean("postload", false);
                    recipeTypes.put(id, new PreparedRecipeType(id, gui.structure(), gui.provider(), loader, postLoad));
                } catch (Exception e) {
                    StackFormatter.handle(e);
                }}
            } catch (Exception e) {
                StackFormatter.handle(e);
            }}

            return this;
        });
    }
}

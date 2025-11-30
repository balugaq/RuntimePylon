package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PreRegister;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.config.StackFormatter;
import com.balugaq.runtimepylon.config.preloads.PreparedRecipeType;
import com.balugaq.runtimepylon.exceptions.IncompatibleKeyFormatException;
import com.balugaq.runtimepylon.exceptions.InvalidDescException;
import com.balugaq.runtimepylon.exceptions.InvalidStructureException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.object.CustomRecipeType;
import com.balugaq.runtimepylon.util.StringUtil;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [Internal Object ID]:
 *   structure:
 *     - "..."
 *   *script: [ScriptDesc]
 *   *postload: boolean
 *   *guiProvider: []
 *   *loader:
 *     [key]: [Adapter Desc]
 * [Adapter Desc]:
 * int
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

                for (String recipeTypeKey : config.getKeys(false)) {try (var ignored1 = StackFormatter.setPosition("Reading key: " + recipeTypeKey)) {
                    if (!recipeTypeKey.matches("[a-z0-9_\\-\\./]+")) throw new IncompatibleKeyFormatException(recipeTypeKey);

                    ConfigurationSection section = config.getConfigurationSection(recipeTypeKey);
                    if (section == null) throw new InvalidDescException(recipeTypeKey);
                    if (PreRegister.blocks(section)) continue;

                    if (!section.contains("structure")) throw new MissingArgumentException("structure");
                    List<String> structure = config.getStringList("structure");
                    structurePrecheck(structure);

                    Map<String, CustomRecipeType.Handler> loader = null;
                    if (section.contains("loader")) {
                        ConfigurationSection sec = section.getConfigurationSection("loader");
                        if (sec != null) {
                            for (String key : sec.getKeys(false)) {
                                var s2 = sec.getConfigurationSection(key);
                                if (s2 != null) {
                                    if (loader == null) loader = new HashMap<>();
                                    loader.put(key, Deserializer.newDeserializer(CustomRecipeType.Handler.class).deserialize(s2));
                                }
                            }
                        }
                    }
                    CustomRecipeType.ItemStackProvider guiProvider = null;
                    // todo

                    var id = InternalObjectID.of(recipeTypeKey).register(namespace);
                    ScriptDesc scriptdesc = Pack.readOrNull(section, ScriptDesc.class, "script");
                    boolean postLoad = section.getBoolean("postload", false);
                    recipeTypes.put(id, new PreparedRecipeType(id, structure, guiProvider, loader, scriptdesc, postLoad));
                } catch (Exception e) {
                    StackFormatter.handle(e);
                }}
            } catch (Exception e) {
                StackFormatter.handle(e);
            }}

            return this;
        });
    }

    public void structurePrecheck(List<String> list) throws InvalidStructureException {
        var e = new InvalidStructureException(list);
        if (list.size() > 5) throw e;
    }
}

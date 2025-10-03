package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.pack.Author;
import com.balugaq.runtimepylon.config.pack.Blocks;
import com.balugaq.runtimepylon.config.pack.Contributor;
import com.balugaq.runtimepylon.config.pack.Fluids;
import com.balugaq.runtimepylon.config.pack.GitHubUpdateLink;
import com.balugaq.runtimepylon.config.pack.Items;
import com.balugaq.runtimepylon.config.pack.PackID;
import com.balugaq.runtimepylon.config.pack.PackNamespace;
import com.balugaq.runtimepylon.config.pack.PackVersion;
import com.balugaq.runtimepylon.config.pack.Pages;
import com.balugaq.runtimepylon.config.pack.Settings;
import com.balugaq.runtimepylon.config.pack.WebsiteLink;
import com.balugaq.runtimepylon.exceptions.DeserializationException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.exceptions.UnrecognizedFileException;
import com.balugaq.runtimepylon.config.pack.Scripts;
import com.balugaq.runtimepylon.script.ScriptExecutor;
import com.balugaq.runtimepylon.util.Debug;
import com.balugaq.runtimepylon.util.MinecraftVersion;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.item.PylonItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * In the disk, we have the following tree structure to define a pack:
 * <ul>
 *   <li>packs/
 *     <ul>
 *       <li>pack_id/
 *         <ul>
 *           <li>pack.yml</li>
 *           <li>lang/</li>
 *           <li>pages</li>
 *           <li>items/</li>
 *           <li>blocks/</li>
 *           <li>fluids/</li>
 *           <li>recipes/</li>
 *           <li>settings/</li>
 *           <li>scripts/</li>
 *         </ul>
 *       </li>
 *     </ul>
 *   </li>
 * </ul>
 * @author balugaq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class Pack implements FileObject<Pack> {
    public static final File settingsFolder = new File(new File(new File(RuntimePylon.getInstance().getDataFolder().getParent(), "PylonCore"), "settings"), "runtimepylon");
    private PackID packID;
    private PackNamespace packNamespace;
    private PackVersion packVersion;
    @Nullable private MinecraftVersion packMinAPIVersion;
    @Nullable private MinecraftVersion packMaxAPIVersion;
    @Nullable private UnsArrayList<PackDesc> loadBefores;
    @Nullable private UnsArrayList<PackDesc> packDependencies;
    @Nullable private UnsArrayList<PluginDesc> pluginDependencies;
    @Nullable private UnsArrayList<Author> authors;
    @Nullable private UnsArrayList<Contributor> contributors;
    @Nullable private UnsArrayList<WebsiteLink> websiteLinks;
    @Nullable private GitHubUpdateLink githubUpdateLink;

    @Nullable private Pages pages;
    @Nullable private Items items;
    @Nullable private Blocks blocks;
    @Nullable private Fluids fluids;
    @Nullable private Recipes recipes;
    @Nullable private Settings settings;
    @Nullable private Scripts scripts;

    @Override
    public List<FileReader<Pack>> readers() {
        return List.of(
                dir -> {
                    List<File> files = Arrays.stream(dir.listFiles()).toList();
                    var meta = files.stream().filter(file -> file.getName().equals("pack.yml")).findFirst().orElse(null);
                    if (meta == null) throw new UnrecognizedFileException(dir.getAbsolutePath());

                    YamlConfiguration config = YamlConfiguration.loadConfiguration(meta);

                    PackID id = read(config, PackID.class, "PackID");
                    PackNamespace namespace = read(config, PackNamespace.class, "PackNamespace");
                    PackVersion version = read(config, PackVersion.class, "PackVersion");
                    MinecraftVersion minAPIVersion = readOrNull(config, MinecraftVersion.class, "PackMinAPIVersion");
                    MinecraftVersion maxAPIVersion = readOrNull(config, MinecraftVersion.class, "PackMaxAPIVersion");
                    UnsArrayList<PackDesc> loadBefores = readGenericOrNull(config, UnsArrayList.class, PackDesc.class, "LoadBefores");
                    UnsArrayList<PackDesc> packDependencies = readGenericOrNull(config, UnsArrayList.class, PackDesc.class, "PackDependencies");
                    UnsArrayList<PluginDesc> pluginDependencies = readGenericOrNull(config, UnsArrayList.class, PluginDesc.class, "PluginDependencies");
                    Author author = readOrNull(config, Author.class, "Author");
                    UnsArrayList<Author> authors = readGenericOrNull(config, UnsArrayList.class, Author.class, "Authors");
                    if (author != null) {
                        if (authors == null) authors = new UnsArrayList<>();
                        authors.add(author);
                    }
                    UnsArrayList<Contributor> contributors = readGenericOrNull(config, UnsArrayList.class, Contributor.class, "Contributors");
                    UnsArrayList<WebsiteLink> websiteLinks = readGenericOrNull(config, UnsArrayList.class, WebsiteLink.class, "WebsiteLinks");
                    GitHubUpdateLink githubUpdateLink = readOrNull(config, GitHubUpdateLink.class, "GitHubUpdateLink");

                    Pages pages = null;
                    var pagesFolder = findDir(files, "pages");
                    if (pagesFolder != null)
                        pages = new Pages()
                            .setPackNamespace(namespace)
                            .deserialize(pagesFolder);

                    Items items = null;
                    var itemsFolder = findDir(files, "items");
                    if (itemsFolder != null)
                        items = new Items()
                            .setPackNamespace(namespace)
                            .deserialize(itemsFolder);

                    Blocks blocks = null;
                    var blocksFolder = findDir(files, "blocks");
                    if (blocksFolder != null)
                        blocks = new Blocks()
                                .setPackNamespace(namespace)
                                .deserialize(blocksFolder);

                    Fluids fluids = null;
                    var fluidsFolder = findDir(files, "fluids");
                    if (fluidsFolder != null)
                        fluids = new Fluids()
                            .setPackNamespace(namespace)
                            .deserialize(fluidsFolder);

                    Recipes recipes = null;
                    var recipesFolder = findDir(files, "recipes");
                    if (recipesFolder != null)
                        recipes = new Recipes()
                            .setPackNamespace(namespace)
                            .deserialize(recipesFolder);

                    Settings settings = null;
                    var settingsFolder = findDir(files, "settings");
                    if (settingsFolder != null)
                        settings = new Settings(settingsFolder, namespace);

                    Scripts scripts = null;
                    var scriptsFolder = findDir(files, "scripts");
                    if (scriptsFolder != null)
                        scripts = new Scripts()
                            .deserialize(scriptsFolder);

                    return new Pack(
                            id,
                            namespace,
                            version,
                            minAPIVersion,
                            maxAPIVersion,
                            loadBefores,
                            packDependencies,
                            pluginDependencies,
                            authors,
                            contributors,
                            websiteLinks,
                            githubUpdateLink,
                            pages,
                            items,
                            blocks,
                            fluids,
                            recipes,
                            settings,
                            scripts
                    );
                }
        );
    }

    private void registerSettings() {
        if (settings == null) return;
        settings.merge(settingsFolder);
    }

    private void registerPages() {
        if (pages == null) return;
        for (var entry : pages.getPages().entrySet()) {
            RegisteredObjectID id = entry.getKey();
            Material icon = entry.getValue();
            new SimpleStaticGuidePage(id.getKey(), icon);
            Debug.log("Registered Page: " + id.getKey());
        }
    }

    private void registerItems() {
        if (items == null) return;
        for (var entry : items.getItems().entrySet()) {
            RegisteredObjectID id = entry.getKey();
            ItemStack icon = entry.getValue().getIcon();
            ScriptDesc scriptDesc = entry.getValue().getScript();

            ScriptExecutor executor;
            if (scripts != null && scriptDesc != null) executor = scripts.findScript(scriptDesc);

            if (blocks != null && blocks.getBlocks().containsKey(id)) {
                PylonItem.register(PylonItem.class, icon, id.getKey());
                Debug.log("Registered Item: " + id.getKey());
            } else {
                PylonItem.register(PylonItem.class, icon);
                Debug.log("Registered Item: " + id.getKey());
            }
        }
    }

    private void registerBlocks() {
        if (blocks == null) return;
        for (var entry : blocks.getBlocks().entrySet()) {
            RegisteredObjectID id = entry.getKey();
            Material material = entry.getValue().getMaterial();
            ScriptDesc scriptDesc = entry.getValue().getScript();

            ScriptExecutor executor;
            if (scripts != null && scriptDesc != null) executor = scripts.findScript(scriptDesc);

            PylonBlock.register(id.getKey(), material, PylonBlock.class);
            Debug.log("Registered Block: " + id.getKey());
        }
    }

    private void registerFluids() {
        if (fluids == null) return;
        for (var entry : fluids.getFluids().entrySet()) {
            RegisteredObjectID id = entry.getKey();
            Material material = entry.getValue().getMaterial();
            FluidTemperature temperature = entry.getValue().getTemperature();
            new PylonFluid(id.getKey(), material).addTag(temperature).register();
        }
    }

    private void registerRecipes() {

    }

    public Pack register() {
        registerSettings();
        registerPages();
        registerItems();
        registerBlocks();
        registerFluids();
        registerRecipes();

        return this;
    }

    @Nullable
    public File findDir(List<File> files, String name) {
        return files.stream().filter(file -> file.getName().equals(name) && file.isDirectory()).findFirst().orElse(null);
    }

    public static <T extends Deserializable<T>> T tryExamine(T object) {
        try {
            if (object instanceof Examinable<?> examinable) {
                return (T) examinable.examine();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return object;
    }

    public static <T extends Deserializable<T>> T read(ConfigurationSection config, Class<T> clazz, String path) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        return tryExamine(Deserializable
                .newDeserializer(clazz)
                .deserialize(config.getString(path)));
    }

    @Nullable
    public static <T extends Deserializable<T>> T readOrNull(ConfigurationSection config, Class<T> clazz, String path) {
        try {
            return tryExamine(Deserializable
                    .newDeserializer(clazz)
                    .deserialize(config.getString(path)));
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static <T extends Deserializable<T> & GenericObject<GenericDeserializable<T>, T>, K extends Deserializable<K>> T readGeneric(ConfigurationSection config, Class<T> clazz, Class<K> generic, String path) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        return tryExamine(GenericDeserializable
                .newDeserializer(clazz)
                .setGenericType((Class<T>) generic)
                .deserialize(config.get(path)));
    }

    @Nullable
    public static <T extends Deserializable<T> & GenericObject<GenericDeserializable<T>, T>> T readGenericOrNull(ConfigurationSection config, Class<T> clazz, Class<?> generic, String path) {
        try {
            return tryExamine(GenericDeserializable
                    .newDeserializer(clazz)
                    .setGenericType((Class<T>) generic)
                    .deserialize(config.get(path)));
        } catch (DeserializationException e) {
            return null;
        }
    }
}

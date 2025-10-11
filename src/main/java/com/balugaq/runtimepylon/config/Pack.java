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
import com.balugaq.runtimepylon.config.pack.Recipes;
import com.balugaq.runtimepylon.config.pack.Saveditems;
import com.balugaq.runtimepylon.config.pack.Scripts;
import com.balugaq.runtimepylon.config.pack.Settings;
import com.balugaq.runtimepylon.config.pack.WebsiteLink;
import com.balugaq.runtimepylon.exceptions.DeserializationException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.exceptions.MissingFileException;
import com.balugaq.runtimepylon.exceptions.PackException;
import com.balugaq.runtimepylon.script.ScriptExecutor;
import com.balugaq.runtimepylon.util.Debug;
import com.balugaq.runtimepylon.util.MinecraftVersion;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.item.PylonItem;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
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
 *           <li>pages/</li>
 *           <li>items/</li>
 *           <li>blocks/</li>
 *           <li>fluids/</li>
 *           <li>recipes/</li>
 *           <li>settings/</li>
 *           <li>scripts/</li>
 *           <li>saveditems/</li>
 *         </ul>
 *       </li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * @author balugaq
 */
@SuppressWarnings({"unchecked", "RegExpRedundantEscape", "ResultOfMethodCallIgnored", "UnusedAssignment", "unused"})
@Data
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class Pack implements FileObject<Pack> {
    public static final File pylonCore = new File(RuntimePylon.getInstance().getDataFolder().getParent(), "PylonCore");
    public static final File settingsFolder = new File(new File(pylonCore, "settings"), RuntimePylon.getInstance().namespace());
    public static final File recipesFolder = new File(pylonCore, "recipes");
    public static final File langFolder = new File(new File(pylonCore, "lang"), RuntimePylon.getInstance().namespace());
    private final File dir;
    private final PackID packID;
    private final PackNamespace packNamespace;
    private final PackVersion packVersion;
    @Nullable
    private final MinecraftVersion packMinAPIVersion;
    @Nullable
    private final MinecraftVersion packMaxAPIVersion;
    @Nullable
    private final UnsArrayList<PackDesc> packLoadBefores;
    @Nullable
    private final UnsArrayList<PackDesc> packSoftDependencies;
    @Nullable
    private final UnsArrayList<PackDesc> packDependencies;
    @Nullable
    private final UnsArrayList<PackDesc> pluginSoftDependencies;
    @Nullable
    private final UnsArrayList<PluginDesc> pluginDependencies;
    @Nullable
    private final UnsArrayList<Author> authors;
    @Nullable
    private final UnsArrayList<Contributor> contributors;
    @Nullable
    private final UnsArrayList<WebsiteLink> websiteLinks;
    @Nullable
    private final GitHubUpdateLink githubUpdateLink;
    @Nullable
    private final Pages pages;
    @Nullable
    private final Items items;
    @Nullable
    private final Blocks blocks;
    @Nullable
    private final Fluids fluids;
    @Nullable
    private final Recipes recipes;
    @Nullable
    private final Settings settings;
    @Nullable
    private final Scripts scripts;
    @Nullable
    private final Saveditems saveditems;

    public static <T extends Deserializer<T>> T tryExamine(T object) {
        try {
            if (object instanceof Examinable<?> examinable) {
                return (T) examinable.examine();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return object;
    }

    public static <T extends Deserializer<T>> T read(ConfigurationSection config, Class<T> clazz, String path) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        return tryExamine(Deserializer
                .newDeserializer(clazz)
                .deserialize(config.getString(path)));
    }

    @Nullable
    public static <T extends Deserializer<T>> T readOrNull(ConfigurationSection config, Class<T> clazz, String path) {
        try {
            return tryExamine(Deserializer
                    .newDeserializer(clazz)
                    .deserialize(config.getString(path)));
        } catch (PackException e) {
            return null;
        }
    }

    public static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T readGeneric(ConfigurationSection config, Class<T> clazz, Class<K> generic, String path) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        return tryExamine(GenericDeserializer
                .newDeserializer(clazz)
                .setGenericType(generic)
                .deserialize(config.get(path)));
    }

    @Nullable
    public static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T readGenericOrNull(ConfigurationSection config, Class<T> clazz, Class<K> generic, String path) {
        try {
            return tryExamine(GenericDeserializer
                    .newDeserializer(clazz)
                    .setGenericType(generic)
                    .deserialize(config.get(path)));
        } catch (PackException e) {
            return null;
        }
    }

    @Override
    public List<FileReader<Pack>> readers() {
        return List.of(
                dir -> {
                    List<File> files = Arrays.stream(dir.listFiles()).toList();
                    var meta = files.stream().filter(file -> file.getName().equals("pack.yml")).findFirst().orElse(null);
                    if (meta == null) throw new MissingFileException(dir.getAbsolutePath() + "/pack.yml");

                    YamlConfiguration config = YamlConfiguration.loadConfiguration(meta);

                    PackID id = read(config, PackID.class, "PackID");
                    PackNamespace namespace = read(config, PackNamespace.class, "PackNamespace");
                    PackVersion version = read(config, PackVersion.class, "PackVersion");
                    MinecraftVersion minAPIVersion = readOrNull(config, MinecraftVersion.class, "PackMinAPIVersion");
                    MinecraftVersion maxAPIVersion = readOrNull(config, MinecraftVersion.class, "PackMaxAPIVersion");
                    UnsArrayList<PackDesc> packLoadBefores = readGenericOrNull(config, UnsArrayList.class, PackDesc.class, "LoadBefores");
                    UnsArrayList<PackDesc> packSoftDependencies = readGenericOrNull(config, UnsArrayList.class, PackDesc.class, "SoftDependencies");
                    UnsArrayList<PackDesc> packDependencies = readGenericOrNull(config, UnsArrayList.class, PackDesc.class, "PackDependencies");
                    UnsArrayList<PackDesc> pluginSoftDependencies = readGenericOrNull(config, UnsArrayList.class, PackDesc.class, "PluginSoftDependencies");
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
                        recipes = new Recipes(recipesFolder, namespace);

                    Settings settings = null;
                    var settingsFolder = findDir(files, "settings");
                    if (settingsFolder != null)
                        settings = new Settings(settingsFolder, namespace);

                    Scripts scripts = null;
                    var scriptsFolder = findDir(files, "scripts");
                    if (scriptsFolder != null)
                        scripts = new Scripts()
                                .deserialize(scriptsFolder);

                    Saveditems saveditems = null;
                    var saveditemsFolder = findDir(files, "saveditems");
                    if (saveditemsFolder != null)
                        saveditems = new Saveditems()
                                .deserialize(saveditemsFolder);

                    return new Pack(
                            dir,
                            id,
                            namespace,
                            version,
                            minAPIVersion,
                            maxAPIVersion,
                            packLoadBefores,
                            packSoftDependencies,
                            packDependencies,
                            pluginSoftDependencies,
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
                            scripts,
                            saveditems
                    );
                }
        );
    }

    private void loadLang(@Nullable File from, File to) {
        if (from == null) return;
        if (!from.exists()) from.mkdir();

        for (File file : from.listFiles()) {
            if (file.isFile() && file.getName().matches("[a-z0-9_\\-\\./]+\\.yml$")) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                File targetFile = new File(to, file.getName());

                if (!targetFile.exists()) {
                    try {
                        targetFile.createNewFile();
                    } catch (IOException e) {
                        Debug.severe(e);
                        continue;
                    }
                }

                YamlConfiguration targetConfig = YamlConfiguration.loadConfiguration(targetFile);
                ConfigurationSection item = config.getConfigurationSection("item");
                if (item != null) {
                    for (String key : item.getKeys(false)) {
                        targetConfig.set("item." + packNamespace.getNamespace() + "_" + key, item.get(key));
                    }
                }
                ConfigurationSection fluid = config.getConfigurationSection("fluid");
                if (fluid != null) {
                    for (String key : fluid.getKeys(false)) {
                        targetConfig.set("fluid." + packNamespace.getNamespace() + "_" + key, fluid.get(key));
                    }
                }
                ConfigurationSection guidePage = config.getConfigurationSection("guide.page");
                if (guidePage != null) {
                    for (String key : guidePage.getKeys(false)) {
                        targetConfig.set("guide.page." + packNamespace.getNamespace() + "_" + key, guidePage.get(key));
                    }
                }
                try {
                    targetConfig.save(targetFile);
                } catch (IOException e) {
                    Debug.severe(e);
                }
            } else if (file.isDirectory()) {
                loadLang(file, new File(to, file.getName()));
            }
        }
    }

    private void registerSettings() {
        if (settings == null) return;
        settings.mergeTo(settingsFolder);
    }

    private void registerPages() {
        if (pages == null) return;
        for (var entry : pages.getPages().values()) {
            PackManager.load(entry, e -> {
                RegisteredObjectID id = e.getId();
                Material icon = e.getMaterial();
                new SimpleStaticGuidePage(id.getKey(), icon);
                Debug.log("Registered Page: " + id.getKey());
            });
        }
    }

    private void registerItems() {
        if (items == null) return;
        for (var entry : items.getItems().values()) {
            PackManager.load(entry, e -> {
                RegisteredObjectID id = e.getId();
                ItemStack icon = entry.getIcon();
                ScriptDesc scriptDesc = entry.getScript();

                ScriptExecutor executor;
                if (scripts != null && scriptDesc != null) executor = scripts.findScript(scriptDesc);

                if (blocks != null && blocks.getBlocks().containsKey(id)) {
                    PylonItem.register(PylonItem.class, icon, id.getKey());
                    Debug.log("Registered Item: " + id.getKey());
                } else {
                    PylonItem.register(PylonItem.class, icon);
                    Debug.log("Registered Item: " + id.getKey());
                }
            });
        }
    }

    private void registerBlocks() {
        if (blocks == null) return;
        for (var entry : blocks.getBlocks().values()) {
            PackManager.load(entry, e -> {
                RegisteredObjectID id = e.getId();
                Material material = e.getMaterial();
                ScriptDesc scriptDesc = e.getScript();

                ScriptExecutor executor;
                if (scripts != null && scriptDesc != null) executor = scripts.findScript(scriptDesc);

                PylonBlock.register(id.getKey(), material, PylonBlock.class);
                Debug.log("Registered Block: " + id.getKey());
            });
        }
    }

    private void registerFluids() {
        if (fluids == null) return;
        for (var entry : fluids.getFluids().values()) {
            PackManager.load(entry, e -> {
                RegisteredObjectID id = e.getId();
                Material material = e.getMaterial();
                FluidTemperature temperature = e.getTemperature();
                new PylonFluid(id.getKey(), material).addTag(temperature).register();
            });
        }
    }

    private void registerRecipes() {
        if (recipes == null) return;
        recipes.mergeTo(recipesFolder);
    }

    public Pack register() {
        loadLang(findDir(Arrays.asList(dir.listFiles()), "lang"), langFolder);
        registerSettings();
        registerRecipes();
        registerPages();
        registerItems();
        registerBlocks();
        registerFluids();
        return this;
    }

    @Nullable
    public File findDir(List<File> files, String name) {
        return files.stream().filter(file -> file.getName().equals(name) && file.isDirectory()).findFirst().orElse(null);
    }
}

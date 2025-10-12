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
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.exceptions.MissingFileException;
import com.balugaq.runtimepylon.exceptions.PackException;
import com.balugaq.runtimepylon.script.ScriptExecutor;
import com.balugaq.runtimepylon.util.Debug;
import com.balugaq.runtimepylon.util.MinecraftVersion;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
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
import java.util.function.Function;

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
    private final UnsArrayList<PluginDesc> pluginSoftDependencies;
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
    // todo: supportedLanguages

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
        return read(config, clazz, path, t -> t);
    }

    public static <T extends Deserializer<T>> T read(ConfigurationSection config, Class<T> clazz, String path, Advancer<T> advancer) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        return tryExamine(advancer.advance(Deserializer.newDeserializer(clazz))
                .deserialize(config.getString(path)));
    }

    @Nullable
    public static <T extends Deserializer<T>> T readOrNull(ConfigurationSection config, Class<T> clazz, String path) {
        return readOrNull(config, clazz, path, t -> t);
    }

    @Nullable
    public static <T extends Deserializer<T>> T readOrNull(ConfigurationSection config, Class<T> clazz, String path, Advancer<T> advancer) {
        try {
            return tryExamine(advancer.advance(Deserializer.newDeserializer(clazz))
                    .deserialize(config.getString(path)));
        } catch (PackException e) {
            return null;
        }
    }

    public static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T read(ConfigurationSection config, Class<T> clazz, Class<K> generic, String path) {
        return read(config, clazz, generic, path, t -> t);
    }

    public static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T read(ConfigurationSection config, Class<T> clazz, Class<K> generic, String path, Advancer<T> advancer) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        return tryExamine(advancer.advance(GenericDeserializer.newDeserializer(clazz).setGenericType(generic))
                .deserialize(config.get(path)));
    }

    @Nullable
    public static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T readOrNull(ConfigurationSection config, Class<T> clazz, Class<K> generic, String path) {
        return readOrNull(config, clazz, generic, path, t -> t);
    }

    @Nullable
    public static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T readOrNull(ConfigurationSection config, Class<T> clazz, Class<K> generic, String path, Advancer<K> advancer) {
        try {
            return tryExamine(GenericDeserializer
                    .newDeserializer(clazz)
                    .setGenericType(generic)
                    .setAdvancer(advancer)
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
                    UnsArrayList<PackDesc> packLoadBefores = readOrNull(config, UnsArrayList.class, PackDesc.class, "LoadBefores");
                    UnsArrayList<PackDesc> packSoftDependencies = readOrNull(config, UnsArrayList.class, PackDesc.class, "SoftDependencies");
                    UnsArrayList<PackDesc> packDependencies = readOrNull(config, UnsArrayList.class, PackDesc.class, "PackDependencies");
                    UnsArrayList<PluginDesc> pluginSoftDependencies = readOrNull(config, UnsArrayList.class, PluginDesc.class, "PluginSoftDependencies");
                    UnsArrayList<PluginDesc> pluginDependencies = readOrNull(config, UnsArrayList.class, PluginDesc.class, "PluginDependencies");
                    Author author = readOrNull(config, Author.class, "Author");
                    UnsArrayList<Author> authors = readOrNull(config, UnsArrayList.class, Author.class, "Authors");
                    if (author != null) {
                        if (authors == null) authors = new UnsArrayList<>();
                        authors.add(author);
                    }
                    UnsArrayList<Contributor> contributors = readOrNull(config, UnsArrayList.class, Contributor.class, "Contributors");
                    UnsArrayList<WebsiteLink> websiteLinks = readOrNull(config, UnsArrayList.class, WebsiteLink.class, "WebsiteLinks");
                    GitHubUpdateLink githubUpdateLink = readOrNull(config, GitHubUpdateLink.class, "GitHubUpdateLink");

                    StackWalker.setPosition("Reading pages");
                    Pages pages = null;
                    var pagesFolder = findDir(files, "pages");
                    if (pagesFolder != null)
                        pages = new Pages()
                                .setPackNamespace(namespace)
                                .deserialize(pagesFolder);
                    StackWalker.destroy();

                    StackWalker.setPosition("Reading items");
                    Items items = null;
                    var itemsFolder = findDir(files, "items");
                    if (itemsFolder != null)
                        items = new Items()
                                .setPackNamespace(namespace)
                                .deserialize(itemsFolder);
                    StackWalker.destroy();

                    StackWalker.setPosition("Reading blocks");
                    Blocks blocks = null;
                    var blocksFolder = findDir(files, "blocks");
                    if (blocksFolder != null)
                        blocks = new Blocks()
                                .setPackNamespace(namespace)
                                .deserialize(blocksFolder);
                    StackWalker.destroy();

                    StackWalker.setPosition("Reading fluids");
                    Fluids fluids = null;
                    var fluidsFolder = findDir(files, "fluids");
                    if (fluidsFolder != null)
                        fluids = new Fluids()
                                .setPackNamespace(namespace)
                                .deserialize(fluidsFolder);
                    StackWalker.destroy();

                    StackWalker.setPosition("Reading recipes");
                    Recipes recipes = null;
                    var recipesFolder = findDir(files, "recipes");
                    if (recipesFolder != null)
                        recipes = new Recipes(recipesFolder, namespace);
                    StackWalker.destroy();

                    StackWalker.setPosition("Reading settings");
                    Settings settings = null;
                    var settingsFolder = findDir(files, "settings");
                    if (settingsFolder != null)
                        settings = new Settings(settingsFolder, namespace);
                    StackWalker.destroy();

                    StackWalker.setPosition("Reading scripts");
                    Scripts scripts = null;
                    var scriptsFolder = findDir(files, "scripts");
                    if (scriptsFolder != null)
                        scripts = new Scripts()
                                .deserialize(scriptsFolder);
                    StackWalker.destroy();

                    StackWalker.setPosition("Reading saveditems");
                    Saveditems saveditems = null;
                    var saveditemsFolder = findDir(files, "saveditems");
                    if (saveditemsFolder != null)
                        saveditems = new Saveditems()
                                .deserialize(saveditemsFolder);
                    StackWalker.destroy();

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
                        targetConfig.set("item." + ExternalObjectID.of(packNamespace, InternalObjectID.of(key)).id(), item.get(key));
                    }
                }
                ConfigurationSection fluid = config.getConfigurationSection("fluid");
                if (fluid != null) {
                    for (String key : fluid.getKeys(false)) {
                        targetConfig.set("fluid." + ExternalObjectID.of(packNamespace, InternalObjectID.of(key)).id(), fluid.get(key));
                    }
                }
                ConfigurationSection guidePage = config.getConfigurationSection("guide.page");
                if (guidePage != null) {
                    for (String key : guidePage.getKeys(false)) {
                        targetConfig.set("guide.page." + ExternalObjectID.of(packNamespace, InternalObjectID.of(key)).id(), guidePage.get(key));
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
                RegisteredObjectID id = e.id();
                try (var sk = StackWalker.setPosition("Loading page: " + id)) {
                    Material icon = e.material();
                    SimpleStaticGuidePage page = new SimpleStaticGuidePage(id.key(), icon);
                    if (e.parents() == null) {
                        PylonGuide.getRootPage().addPage(page);
                    } else {
                        for (var parent : e.parents()) {
                            parent.getPage().addPage(page);
                        }
                    }
                    Debug.log("Registered Page: " + id.key());
                } catch (Exception ex) {
                    StackWalker.handle(ex);
                }
            });
        }
    }

    private void registerItems() {
        if (items == null) return;
        for (var entry : items.getItems().values()) {
            PackManager.load(entry, e -> {
                RegisteredObjectID id = e.id();
                try (var sk = StackWalker.setPosition("Loading item: " + id)) {
                    ItemStack icon = entry.icon();
                    ScriptDesc scriptDesc = entry.script();

                    ScriptExecutor executor;
                    if (scripts != null && scriptDesc != null) executor = scripts.findScript(scriptDesc);

                    if (blocks != null && blocks.getBlocks().containsKey(id)) {
                        PylonItem.register(PylonItem.class, icon, id.key());
                        Debug.log("Registered Item: " + id.key());
                    } else {
                        PylonItem.register(PylonItem.class, icon);
                        Debug.log("Registered Item: " + id.key());
                    }

                    List<PageDesc> pages = e.pages();
                    if (pages != null) pages.forEach(desc -> desc.getPage().addItem(e.icon()));
                } catch (Exception ex) {
                    StackWalker.handle(ex);
                }
            });
        }
    }

    private void registerBlocks() {
        if (blocks == null) return;
        for (var entry : blocks.getBlocks().values()) {
            PackManager.load(entry, e -> {
                RegisteredObjectID id = e.id();
                try (var sk = StackWalker.setPosition("Loading block: " + id)) {
                    Material material = e.material();
                    ScriptDesc scriptDesc = e.script();

                    ScriptExecutor executor;
                    if (scripts != null && scriptDesc != null) executor = scripts.findScript(scriptDesc);

                    PylonBlock.register(id.key(), material, PylonBlock.class);
                    Debug.log("Registered Block: " + id.key());
                } catch (Exception ex) {
                    StackWalker.handle(ex);
                }
            });
        }
    }

    private void registerFluids() {
        if (fluids == null) return;
        for (var entry : fluids.getFluids().values()) {
            PackManager.load(entry, e -> {
                RegisteredObjectID id = e.id();
                Material material = e.material();
                FluidTemperature temperature = e.temperature();
                PylonFluid fluid = new PylonFluid(id.key(), material).addTag(temperature);
                fluid.register();

                List<PageDesc> pages = e.pages();
                if (pages != null) pages.forEach(desc -> desc.getPage().addFluid(fluid));
            });
        }
    }

    private void registerRecipes() {
        if (recipes == null) return;
        recipes.mergeTo(recipesFolder);
    }

    public Pack register() {
        StackWalker.runWith("Loading lang", () -> loadLang(findDir(Arrays.asList(dir.listFiles()), "lang"), langFolder));
        StackWalker.runWith("Loading settings", this::registerSettings);
        StackWalker.runWith("Loading recipes", this::registerRecipes);
        StackWalker.runWith("Loading pages", this::registerPages);
        StackWalker.runWith("Loading items", this::registerItems);
        StackWalker.runWith("Loading blocks", this::registerBlocks);
        StackWalker.runWith("Loading fluids", this::registerFluids);
        return this;
    }

    @Nullable
    public File findDir(List<File> files, String name) {
        return files.stream().filter(file -> file.getName().equals(name) && file.isDirectory()).findFirst().orElse(null);
    }

    @FunctionalInterface
    public interface Advancer<T> extends Function<T, T> {
        default T apply(T object) {
            return advance(object);
        }

        T advance(T object);
    }
}

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
import com.balugaq.runtimepylon.config.pack.RecipeTypes;
import com.balugaq.runtimepylon.config.pack.Recipes;
import com.balugaq.runtimepylon.config.pack.Saveditems;
import com.balugaq.runtimepylon.config.pack.Scripts;
import com.balugaq.runtimepylon.config.pack.Settings;
import com.balugaq.runtimepylon.config.pack.WebsiteLink;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.exceptions.MissingFileException;
import com.balugaq.runtimepylon.exceptions.PackException;
import com.balugaq.runtimepylon.exceptions.UnknownEnumException;
import com.balugaq.runtimepylon.object.CustomBlock;
import com.balugaq.runtimepylon.object.CustomFluid;
import com.balugaq.runtimepylon.object.CustomItem;
import com.balugaq.runtimepylon.object.CustomPage;
import com.balugaq.runtimepylon.object.CustomRecipe;
import com.balugaq.runtimepylon.object.CustomRecipeType;
import com.balugaq.runtimepylon.object.PackAddon;
import com.balugaq.runtimepylon.script.ScriptExecutor;
import com.balugaq.runtimepylon.util.Debug;
import com.balugaq.runtimepylon.util.MinecraftVersion;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
 *           <li>recipe_types</li>
 *           <li>recipes/</li>
 *           <li>multiblocks</li> // todo: loadRecipeType:
 *           <li>machines</li> // todo: loadRecipeType:
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
@Slf4j
@SuppressWarnings({"unchecked", "RegExpRedundantEscape", "ResultOfMethodCallIgnored", "UnusedAssignment", "unused"})
@Data
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class Pack implements FileObject<Pack> {
    public static final File pylonCore = new File(RuntimePylon.getInstance().getDataFolder().getParent(), "PylonCore");
    private final File dir;
    private final PackID packID;
    private final PackNamespace packNamespace;
    private final PackVersion packVersion;
    @Nullable
    private final MinecraftVersion packMinAPIVersion;
    @Nullable
    private final MinecraftVersion packMaxAPIVersion;
    @Nullable
    private final MyArrayList<PackDesc> packLoadBefores;
    @Nullable
    private final MyArrayList<PackDesc> packSoftDependencies;
    @Nullable
    private final MyArrayList<PackDesc> packDependencies;
    @Nullable
    private final MyArrayList<PluginDesc> pluginDependencies;
    @Nullable
    private final MyArrayList<Author> authors;
    @Nullable
    private final MyArrayList<Contributor> contributors;
    @Nullable
    private final MyArrayList<WebsiteLink> websiteLinks;
    @Nullable
    private final GitHubUpdateLink githubUpdateLink;
    @Nullable
    private final MyArrayList<Language> languages;
    @Nullable
    private final Pages pages;
    @Nullable
    private final Items items;
    @Nullable
    private final Blocks blocks;
    @Nullable
    private final Fluids fluids;
    @Nullable
    private final RecipeTypes recipeTypes;
    @Nullable
    private final Recipes recipes;
    @Nullable
    private final Settings settings;
    @Nullable
    private final Scripts scripts;
    @Nullable
    private final Saveditems saveditems;

    public static <T extends Enum<T>> T readEnum(ConfigurationSection config, Class<T> clazz, String path) {
        return readEnum(config, clazz, path, t -> t);
    }

    public static <T extends Enum<T>> T readEnum(ConfigurationSection config, Class<T> clazz, String path, Advancer<Deserializer.EnumDeserializer<T>> advancer) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        String s = config.getString(path);
        if (s == null) throw new MissingArgumentException(clazz);
        T value = advancer.advance(Deserializer.enumDeserializer(clazz))
                .deserialize(s);
        if (value == null) throw new UnknownEnumException(clazz, s);
        return value;
    }

    @Nullable
    public static <T extends Enum<T>> T readEnumOrNull(ConfigurationSection config, Class<T> clazz, String path) {
        return readEnumOrNull(config, clazz, path, t -> t);
    }

    @Nullable
    public static <T extends Enum<T>> T readEnumOrNull(ConfigurationSection config, Class<T> clazz, String path, Advancer<Deserializer.EnumDeserializer<T>> advancer) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        return advancer.advance(Deserializer.enumDeserializer(clazz))
                .deserialize(config.getString(path));
    }

    public static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T read(ConfigurationSection config, Class<T> clazz, Class<K> generic, String path) {
        return read(config, clazz, generic, path, t -> t);
    }

    public static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T read(ConfigurationSection config, Class<T> clazz, Class<K> generic, String path, Advancer<T> advancer) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        return tryExamine(advancer.advance(GenericDeserializer.newDeserializer(clazz).setGenericType(generic))
                                  .deserialize(config.get(path)));
    }

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

    @Override
    public List<FileReader<Pack>> readers() {
        return List.of(dir -> {
            List<File> files = Arrays.stream(dir.listFiles()).toList();
            var meta = files.stream().filter(file -> file.getName().equals("pack.yml")).findFirst().orElse(null);
            if (meta == null) throw new MissingFileException(dir.getAbsolutePath() + "/pack.yml");

            try (var ignored = StackFormatter.setPosition("Reading file: pack.yml")) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(meta);

                PackID id = read(config, PackID.class, "id");
                PackVersion version = read(config, PackVersion.class, "version");
                MinecraftVersion minAPIVersion = readOrNull(config, MinecraftVersion.class, "minAPIVersion");
                MinecraftVersion maxAPIVersion = readOrNull(config, MinecraftVersion.class, "maxAPIVersion");
                MyArrayList<PackDesc> packLoadBefores = readOrNull(config, MyArrayList.class, PackDesc.class, "loadBefores");
                MyArrayList<PackDesc> packSoftDependencies = readOrNull(config, MyArrayList.class, PackDesc.class, "packSoftDependencies");
                MyArrayList<PackDesc> packDependencies = readOrNull(config, MyArrayList.class, PackDesc.class, "packDependencies");
                MyArrayList<PluginDesc> pluginDependencies = readOrNull(config, MyArrayList.class, PluginDesc.class, "pluginDependencies");
                Author author = readOrNull(config, Author.class, "author");
                MyArrayList<Author> authors = readOrNull(config, MyArrayList.class, Author.class, "authors");
                if (author != null) {
                    if (authors == null) authors = new MyArrayList<>();
                    if (!authors.contains(author)) {
                        authors.add(author);
                    }
                }
                MyArrayList<Contributor> contributors = readOrNull(config, MyArrayList.class, Contributor.class, "contributors");
                MyArrayList<WebsiteLink> websiteLinks = readOrNull(config, MyArrayList.class, WebsiteLink.class, "websiteLinks");
                GitHubUpdateLink githubUpdateLink = readOrNull(config, GitHubUpdateLink.class, "githubUpdateLink");
                MyArrayList<Language> languages = readOrNull(config, MyArrayList.class, Language.class, "languages");
                Set<Locale> locales = new HashSet<>();
                if (languages != null) {
                    locales.addAll(languages.stream().map(Language::locale).toList());
                } else {
                    locales.add(Locale.ENGLISH);
                }
                RuntimePylon.getInstance().addSupportedLanguages(locales);
                Material material = Pack.readEnum(config, Material.class, "material", Deserializer.EnumDeserializer::forceUpperCase);
                PackNamespace namespace = PackNamespace.warp(id, locales, material);

                StackFormatter.setPosition("Reading pages");
                Pages pages = null;
                var pagesFolder = findDir(files, "pages");
                if (pagesFolder != null)
                    pages = new Pages()
                            .setPackNamespace(namespace)
                            .deserialize(pagesFolder);
                StackFormatter.destroy();

                StackFormatter.setPosition("Reading items");
                Items items = null;
                var itemsFolder = findDir(files, "items");
                if (itemsFolder != null)
                    items = new Items()
                            .setPackNamespace(namespace)
                            .deserialize(itemsFolder);
                StackFormatter.destroy();

                StackFormatter.setPosition("Reading blocks");
                Blocks blocks = null;
                var blocksFolder = findDir(files, "blocks");
                if (blocksFolder != null)
                    blocks = new Blocks()
                            .setPackNamespace(namespace)
                            .deserialize(blocksFolder);
                StackFormatter.destroy();

                StackFormatter.setPosition("Reading fluids");
                Fluids fluids = null;
                var fluidsFolder = findDir(files, "fluids");
                if (fluidsFolder != null)
                    fluids = new Fluids()
                            .setPackNamespace(namespace)
                            .deserialize(fluidsFolder);
                StackFormatter.destroy();

                StackFormatter.setPosition("Reading Recipe Types");
                RecipeTypes recipeTypes = null;
                var recipesTypesFolder = findDir(files, "recipe_types");
                if (recipesTypesFolder != null)
                    recipeTypes = new RecipeTypes()
                                 .setPackNamespace(namespace)
                                 .deserialize(recipesTypesFolder);
                StackFormatter.destroy();

                StackFormatter.setPosition("Reading recipes");
                Recipes recipes = null;
                var recipesFolder = findDir(files, "recipes");
                if (recipesFolder != null)
                    recipes = new Recipes(recipesFolder, namespace);
                StackFormatter.destroy();

                StackFormatter.setPosition("Reading settings");
                Settings settings = null;
                var settingsFolder = findDir(files, "settings");
                if (settingsFolder != null)
                    settings = new Settings(settingsFolder, namespace);
                StackFormatter.destroy();

                StackFormatter.setPosition("Reading scripts");
                Scripts scripts = null;
                var scriptsFolder = findDir(files, "scripts");
                if (scriptsFolder != null)
                    scripts = new Scripts()
                            .deserialize(scriptsFolder);
                StackFormatter.destroy();

                StackFormatter.setPosition("Reading saveditems");
                Saveditems saveditems = null;
                var saveditemsFolder = findDir(files, "saveditems");
                if (saveditemsFolder != null)
                    saveditems = new Saveditems()
                            .deserialize(saveditemsFolder);
                StackFormatter.destroy();

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
                        pluginDependencies,
                        authors,
                        contributors,
                        websiteLinks,
                        githubUpdateLink,
                        languages,
                        pages,
                        items,
                        blocks,
                        fluids,
                        recipeTypes,
                        recipes,
                        settings,
                        scripts,
                        saveditems
                );
            } catch (Exception e) {
                StackFormatter.handle(e);
            }

            return this;
        });
    }

    public static <T extends Deserializer<T>> T read(ConfigurationSection config, Class<T> clazz, String path) {
        return read(config, clazz, path, t -> t);
    }

    @Nullable
    public static <T extends Deserializer<T>> T readOrNull(ConfigurationSection config, Class<T> clazz, String path) {
        return readOrNull(config, clazz, path, t -> t);
    }

    @Nullable
    public static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T readOrNull(ConfigurationSection config, Class<T> clazz, Class<K> generic, String path) {
        return readOrNull(config, clazz, generic, path, t -> t);
    }

    @Nullable
    public File findDir(List<File> files, String name) {
        return files.stream().filter(file -> file.getName().equals(name) && file.isDirectory()).findFirst().orElse(null);
    }

    public static <T extends Deserializer<T>> T read(ConfigurationSection config, Class<T> clazz, String path, Advancer<T> advancer) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        return tryExamine(advancer.advance(Deserializer.newDeserializer(clazz))
                                  .deserialize(config.getString(path)));
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

    private void loadLang(@Nullable File from, File to) {
        if (from == null) return;
        if (!from.exists()) from.mkdir();
        if (!to.exists()) to.mkdir();

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

                PackManager.saveConfig(config, YamlConfiguration.loadConfiguration(targetFile), targetFile);
            } else if (file.isDirectory()) {
                loadLang(file, new File(to, file.getName()));
            }
        }
    }

    private void registerSettings() {
        if (settings == null) return;
        settings.mergeTo(getSettingsFolder());
    }

    public File getSettingsFolder() {
        return new File(new File(pylonCore, "settings"), plugin().namespace());
    }

    private void registerPages() {
        if (pages == null) return;
        for (var entry : pages.getPages().values()) {
            PackManager.load(
                    entry, e -> {
                        RegisteredObjectID id = e.id();
                        try (var sk = StackFormatter.setPosition("Loading page: " + id)) {
                            ScriptDesc scriptDesc = entry.script();

                            ScriptExecutor executor;
                            if (scripts != null && scriptDesc != null) executor = scripts.findScript(scriptDesc);
                            // todo: executor

                            Material icon = e.material();
                            CustomPage page = new CustomPage(id.key(), icon);
                            if (e.parents() == null) {
                                PylonGuide.getRootPage().addPage(page);
                            } else {
                                for (var parent : e.parents()) {
                                    parent.getPage().addPage(page);
                                }
                            }
                            RuntimePylon.getInstance().registerCustomPage(page);
                            Debug.log("Registered Page: " + id.key());
                        } catch (Exception ex) {
                            StackFormatter.handle(ex);
                        }
                    }
            );
        }
    }

    private void registerItems() {
        if (items == null) return;
        for (var entry : items.getItems().values()) {
            PackManager.load(
                    entry, e -> {
                        RegisteredObjectID id = e.id();
                        try (var sk = StackFormatter.setPosition("Loading item: " + id)) {
                            ItemStack icon = entry.icon();
                            ScriptDesc scriptDesc = entry.script();

                            ScriptExecutor executor;
                            if (scripts != null && scriptDesc != null) executor = scripts.findScript(scriptDesc);
                            // todo: executor

                            if (blocks != null && blocks.getBlocks().containsKey(id)) {
                                CustomItem.register(CustomItem.class, icon, id.key());
                                Debug.log("Registered Item: " + id.key());
                            } else {
                                CustomItem.register(CustomItem.class, icon);
                                Debug.log("Registered Item: " + id.key());
                            }

                            List<PageDesc> descs = e.pages();
                            if (descs != null) descs.forEach(desc -> {
                                try (var ignored = StackFormatter.setPosition("Adding to page: " + desc.getKey())) {
                                    desc.getPage().addItem(e.icon());
                                } catch (Exception ex) {
                                    StackFormatter.handle(ex);
                                }
                            });
                        } catch (Exception ex) {
                            StackFormatter.handle(ex);
                        }
                    }
            );
        }
    }

    private void registerBlocks() {
        if (blocks == null) return;
        for (var entry : blocks.getBlocks().values()) {
            PackManager.load(
                    entry, e -> {
                        RegisteredObjectID id = e.id();
                        try (var sk = StackFormatter.setPosition("Loading block: " + id)) {
                            Material material = e.material();
                            ScriptDesc scriptDesc = e.script();

                            ScriptExecutor executor;
                            if (scripts != null && scriptDesc != null) executor = scripts.findScript(scriptDesc);
                            // todo: executor

                            CustomBlock.register(id.key(), material, CustomBlock.class);
                            Debug.log("Registered Block: " + id.key());
                        } catch (Exception ex) {
                            StackFormatter.handle(ex);
                        }
                    }
            );
        }
    }

    private void registerFluids() {
        if (fluids == null) return;
        for (var entry : fluids.getFluids().values()) {
            PackManager.load(
                    entry, e -> {
                        RegisteredObjectID id = e.id();
                        Material material = e.material();
                        FluidTemperature temperature = e.temperature();
                        PylonFluid fluid = new CustomFluid(id.key(), material).addTag(temperature);
                        fluid.register();

                        List<PageDesc> pages = e.pages();
                        if (pages != null) pages.forEach(desc -> desc.getPage().addFluid(fluid));
                    }
            );
        }
    }

    private void registerRecipeTypes() {
        if (recipeTypes == null) return;
        for (var entry : recipeTypes.getRecipeTypes().values()) {
            PackManager.load(
                    entry, e -> {
                        RegisteredObjectID id = e.id();

                        ScriptDesc scriptDesc = e.script();

                        ScriptExecutor executor;
                        if (scripts != null && scriptDesc != null) executor = scripts.findScript(scriptDesc);
                        // todo: executor

                        CustomRecipeType recipeType = new CustomRecipeType(id.key(), e.structure(), e.guiProvider(), e.configReader());
                        recipeType.register();
                    }
            );
        }
    }

    private void registerRecipes() {
        if (recipes == null) return;
        recipes.mergeTo(getRecipesFolder());
    }

    public File getRecipesFolder() {
        return new File(pylonCore, "recipes");
    }

    public File getLangFolder() {
        return new File(new File(pylonCore, "lang"), plugin().namespace());
    }

    public Pack register() {
        PylonRegistry.ADDONS.register(plugin());
        StackFormatter.run("Loading lang", () -> loadLang(findDir(Arrays.asList(dir.listFiles()), "lang"), getLangFolder()));
        StackFormatter.run("Loading settings", this::registerSettings);
        StackFormatter.run("Loading recipes", this::registerRecipes);
        StackFormatter.run("Loading pages", this::registerPages);
        StackFormatter.run("Loading items", this::registerItems);
        StackFormatter.run("Loading blocks", this::registerBlocks);
        StackFormatter.run("Loading fluids", this::registerFluids);
        PylonRegistry.ADDONS.unregister(plugin());
        plugin().registerWithPylon();
        return this;
    }

    public PackAddon plugin() {
        return getPackNamespace().plugin();
    }

    @FunctionalInterface
    public interface Advancer<T> extends Function<T, T> {
        default T apply(T object) {
            return advance(object);
        }

        T advance(T object);
    }
}

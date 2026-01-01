package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.GlobalVars;
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
import com.balugaq.runtimepylon.config.pack.Researches;
import com.balugaq.runtimepylon.config.pack.Saveditems;
import com.balugaq.runtimepylon.config.pack.Scripts;
import com.balugaq.runtimepylon.config.pack.Settings;
import com.balugaq.runtimepylon.config.pack.WebsiteLink;
import com.balugaq.runtimepylon.data.MyArrayList;
import com.balugaq.runtimepylon.exceptions.InvalidDescException;
import com.balugaq.runtimepylon.exceptions.InvalidStructureException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.exceptions.MissingFileException;
import com.balugaq.runtimepylon.exceptions.PackException;
import com.balugaq.runtimepylon.exceptions.UnknownEnumException;
import com.balugaq.runtimepylon.exceptions.UnknownItemException;
import com.balugaq.runtimepylon.manager.PackManager;
import com.balugaq.runtimepylon.object.CustomFluid;
import com.balugaq.runtimepylon.object.CustomGuidePage;
import com.balugaq.runtimepylon.object.CustomRecipeType;
import com.balugaq.runtimepylon.object.ItemStackProvider;
import com.balugaq.runtimepylon.object.PackAddon;
import com.balugaq.runtimepylon.object.blocks.CustomBlock;
import com.balugaq.runtimepylon.object.blocks.CustomMultiBlock;
import com.balugaq.runtimepylon.object.items.CustomItem;
import com.balugaq.runtimepylon.util.Debug;
import com.balugaq.runtimepylon.util.MinecraftVersion;
import io.github.pylonmc.pylon.core.config.PylonConfig;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.research.Research;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
 *           <li>multiblocks</li>
 *           <li>machines</li>
 *           <li>settings/</li>
 *           <li>scripts/</li>
 *           <li>saveditems/</li>
 *           <li>researches/</li>
 *         </ul>
 *       </li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * @author balugaq
 */
@Slf4j
@SuppressWarnings({"unchecked", "RegExpRedundantEscape", "ResultOfMethodCallIgnored", "unused"})
@Data
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class Pack implements FileObject<Pack> {
    public static final File pylonCore = new File(RuntimePylon.getInstance().getDataFolder().getParent(), "PylonCore");
    public static final Item EMPTY = new SimpleItem(ItemStack.empty());
    /**
     * deprecated, use virtual inventory instead.
     * a~z: input item  // "i"
     * 1-9: output item  // "o"
     * B: background item
     * I: input border
     * O: output border
     */
    @Deprecated
    public static final ItemStackProvider DEFAULT_GUI_PROVIDER = (c, r) -> {
        if (r != null) {
            if ('a' <= c && c <= 'z' && c != 'i' && c != 'o') {
                var i = r.getInputs();
                var k = c - 'a';
                if (k >= i.size()) return () -> EMPTY;
                var s = i.get(k);
                if (s instanceof RecipeInput.Item item) return () -> ItemButton.from(item);
                else if (s instanceof RecipeInput.Fluid fluid) return () -> new FluidButton(fluid);
            }
            if ('1' <= c && c <= '9') {
                var o = r.getResults();
                var k = c - '1';
                if (k >= o.size()) return () -> EMPTY;
                var s = o.get(k);
                if (s instanceof FluidOrItem.Item item) return () -> ItemButton.from(item.item());
                else if (s instanceof FluidOrItem.Fluid fluid)
                    return () -> new FluidButton(fluid.amountMillibuckets(), fluid.fluid());
            }
        }
        if (c == 'B') return GuiItems::background;
        if (c == 'I') return GuiItems::input;
        if (c == 'O') return GuiItems::output;
        return () -> EMPTY;
    };
    private final File dir;
    private final PackID packID;
    private final PackNamespace packNamespace;
    private final PackVersion packVersion;
    @Nullable
    private final YamlConfiguration packConfig; // todo
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
    private final Researches researches;
    @Nullable
    private final Scripts scripts;
    @Nullable
    private final Saveditems saveditems;
    private final boolean suppressLanguageMissingWarning;

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
                .deserialize(config.get(path));
    }

    public static <T extends GenericDeserializer<T, K>, K> T read(ConfigurationSection config, Class<T> clazz, Deserializer<K> deserializer, String path) {
        return read(config, clazz, deserializer, path, t -> t);
    }

    public static <T extends GenericDeserializer<T, K>, K> T read(ConfigurationSection config, Class<T> clazz, Deserializer<K> deserializer, String path, Advancer<T> advancer) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        return tryExamine(advancer.advance(GenericDeserializer.newDeserializer(clazz).setDeserializer(deserializer))
                                  .deserialize(config.get(path)));
    }

    public static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T read(ConfigurationSection config, Class<T> clazz, Class<K> generic, String path) {
        return read(config, clazz, generic, path, t -> t);
    }

    public static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T read(ConfigurationSection config, Class<T> clazz, Class<K> generic, String path, Advancer<T> advancer) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        return tryExamine(advancer.advance(GenericDeserializer.newDeserializer(clazz).setGenericType(generic))
                                  .deserialize(config.get(path)));
    }

    public static <T extends BiGenericDeserializer<T, K, M>, K, M> T read(ConfigurationSection config, Class<T> clazz, Class<K> generic, Class<M> generic2, String path) {
        return read(config, clazz, generic, generic2, path, t -> t);
    }

    public static <T extends BiGenericDeserializer<T, K, M>, K, M> T read(ConfigurationSection config, Class<T> clazz, Class<K> generic, Class<M> generic2, String path, Advancer<T> advancer) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        return tryExamine(advancer.advance(BiGenericDeserializer.newDeserializer(clazz).setGenericType(generic).setGenericType2(generic2))
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

    public static void guiStructurePrecheck(List<String> list) throws InvalidStructureException {
        if (list.isEmpty() || list.size() > 5)
            throw new InvalidStructureException(list);
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
                namespace.setScripts(scripts);

                StackFormatter.setPosition("Reading saveditems");
                Saveditems saveditems = null;
                var saveditemsFolder = findDir(files, "saveditems");
                if (saveditemsFolder != null)
                    saveditems = new Saveditems()
                            .deserialize(saveditemsFolder);
                StackFormatter.destroy();

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

                Researches researches = null;
                if (PylonConfig.getResearchesEnabled()) {
                    StackFormatter.setPosition("Reading researches");
                    var researchesFolder = findDir(files, "researches");
                    if (researchesFolder != null)
                        researches = new Researches()
                                .setPackNamespace(namespace)
                                .deserialize(researchesFolder);
                    StackFormatter.destroy();
                }

                boolean suppressLanguageMissingWarning = config.getBoolean("suppressLanguageMissingWarning", false);

                return new Pack(
                        dir,
                        id,
                        namespace,
                        version,
                        new YamlConfiguration(),
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
                        researches,
                        scripts,
                        saveditems,
                        suppressLanguageMissingWarning
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
        return readOrNull(config, clazz, generic, path, k -> k);
    }

    @Nullable
    public static <T extends BiGenericDeserializer<T, K, M>, K extends Deserializer<K>, M extends Deserializer<M>> T readOrNull(ConfigurationSection config, Class<T> clazz, Class<K> generic, Class<M> generic2, String path) {
        return readOrNull(config, clazz, generic, generic2, path, k -> k, m -> m);
    }

    @Nullable
    public File findDir(List<File> files, String name) {
        return files.stream().filter(file -> file.getName().equals(name) && file.isDirectory()).findFirst().orElse(null);
    }

    public static <T extends Deserializer<T>> T read(ConfigurationSection config, Class<T> clazz, String path, Advancer<T> advancer) {
        if (!config.contains(path)) throw new MissingArgumentException(path);
        return tryExamine(advancer.advance(Deserializer.newDeserializer(clazz))
                                  .deserialize(config.get(path)));
    }

    @Nullable
    public static <T extends Deserializer<T>> T readOrNull(ConfigurationSection config, Class<T> clazz, String path, Advancer<T> advancer) {
        try {
            return tryExamine(advancer.advance(Deserializer.newDeserializer(clazz))
                                      .deserialize(config.get(path)));
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
                                      .setAdvancer((Advancer<Deserializer<K>>) advancer)
                                      .deserialize(config.get(path)));
        } catch (PackException e) {
            return null;
        }
    }

    @Nullable
    public static <T extends BiGenericDeserializer<T, K, M>, K, M> T readOrNull(ConfigurationSection config, Class<T> clazz, Class<K> generic, Class<M> generic2, String path, Advancer<Deserializer<K>> advancer, Advancer<Deserializer<M>> advancer2) {
        try {
            return tryExamine(BiGenericDeserializer
                                      .newDeserializer(clazz)
                                      .setGenericType(generic)
                                      .setGenericType2(generic2)
                                      .setAdvancer(advancer)
                                      .setAdvancer2(advancer2)
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
            if (file.isFile() && file.getName().matches("[a-zA-Z0-9_\\-\\./]+\\.yml$")) {
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
            PackManager.load(entry, e -> {
                RegisteredObjectID id = e.id();
                try (var sk = StackFormatter.setPosition("Loading page: " + id)) {
                    Material icon = e.material();
                    CustomGuidePage page = new CustomGuidePage(id.key(), icon);
                    if (e.parents() == null) {
                        PylonGuide.getRootPage().addPage(page);
                    } else {
                        for (var parent : e.parents()) {
                            parent.getPage().addPage(page);
                        }
                    }
                    GlobalVars.putCustomPage(page.getKey(), page);
                    Debug.debug("Registered Page: " + id.key());
                    pages.getLoadedPages().incrementAndGet();
                } catch (Exception ex) {
                    StackFormatter.handle(ex);
                }
            });
        }
    }

    private void registerItems() {
        if (items == null) return;
        for (var entry : items.getItems().values()) {
            PackManager.load(entry, e -> {
                RegisteredObjectID id = e.id();
                try (var sk = StackFormatter.setPosition("Loading item: " + id)) {
                    ItemStack icon = entry.icon();

                    if (blocks != null && blocks.getBlocks().containsKey(id)) {
                        CustomItem.register(CustomItem.class, icon, id.key());
                    } else {
                        CustomItem.register(CustomItem.class, icon);
                    }
                    Debug.debug("Registered Item: " + id.key());
                    items.getLoadedItems().incrementAndGet();

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
            });
        }
    }

    private void registerResearches() {
        if (researches == null) return;
        for (var entry : researches.getResearches().values()) {
            PackManager.load(entry, e -> {
                RegisteredObjectID id = e.id();
                try (var sk = StackFormatter.setPosition("Loading research: " + id)) {
                    String name = e.name() != null ? e.name() : ("pylon." + id.key().getNamespace() +".research." + id.key().getKey());
                    Set<NamespacedKey> unlocks = new HashSet<>();
                    for (String s : e.unlocks()) {
                        var choice = Deserializer.RECIPE_CHOICE.deserializeOrNull(s);
                        if (choice == null) {
                            StackFormatter.handle(new InvalidDescException(s));
                            continue;
                        }

                        for (var item : choice.getChoices()) {
                            PylonItem pylon = PylonItem.fromStack(item);
                            if (pylon == null) {
                                StackFormatter.handle(new UnknownItemException(item.toString()));
                                continue;
                            }

                            unlocks.add(pylon.getKey());
                        }
                    }
                    new Research(id.key(), e.material(), Component.translatable(name), e.cost(), unlocks).register();
                    Debug.debug("Registered Research: " + id.key());
                    researches.getLoadedResearches().incrementAndGet();
                } catch (Exception ex) {
                    StackFormatter.handle(ex);
                }
            });
        }
    }

    private void registerBlocks() {
        if (blocks == null) return;
        for (var entry : blocks.getBlocks().values()) {
            PackManager.load(entry, e -> {
                RegisteredObjectID id = e.id();
                try (var sk = StackFormatter.setPosition("Loading block: " + id)) {
                    Material material = e.material();

                    if (GlobalVars.getMultiBlockComponents(id.key()).isEmpty()) {
                        CustomBlock.register(id.key(), material, CustomBlock.class);
                    } else {
                        CustomMultiBlock.register(id.key(), material, CustomMultiBlock.class);
                    }
                    Debug.debug("Registered Block: " + id.key());
                    blocks.getLoadedBlocks().incrementAndGet();
                } catch (Exception ex) {
                    StackFormatter.handle(ex);
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
                PylonFluid fluid = new CustomFluid(id.key(), material).addTag(temperature);
                fluid.register();
                Debug.debug("Registered Fluid: " + id.key());
                fluids.getLoadedFluids().incrementAndGet();

                List<PageDesc> pages = e.pages();
                if (pages != null) pages.forEach(desc -> desc.getPage().addFluid(fluid));
            });
        }
    }

    private void registerRecipeTypes() {
        if (recipeTypes == null) return;
        for (var entry : recipeTypes.getRecipeTypes().values()) {
            PackManager.load(entry, e -> {
                RegisteredObjectID id = e.id();

                CustomRecipeType recipeType = new CustomRecipeType(id.key(), e.structure(), e.guiProvider(), e.configReader());
                if (e.cloneType() != null) {
                    for (var r : e.cloneType().getRecipes()) {
                        recipeType.addRecipe(r);
                    }
                }
                recipeType.register();
                Debug.debug("Registered RecipeType: " + id.key());
                recipeTypes.getLoadedRecipeTypes().incrementAndGet();
            });
        }
    }

    private void registerRecipes() {
        if (recipes == null) return;
        recipes.loadRecipes();
    }

    public static File getRecipesFolder() {
        return new File(pylonCore, "recipes");
    }

    public File getLangFolder() {
        return new File(new File(pylonCore, "lang"), plugin().namespace());
    }

    public Pack unregister() {
        PackManager.unload(this);
        return this;
    }

    public Pack register() {
        plugin().registerWithPylon();
        StackFormatter.run("Loading lang", () -> loadLang(findDir(Arrays.asList(dir.listFiles()), "lang"), getLangFolder()));
        StackFormatter.run("Loading settings", this::registerSettings);
        StackFormatter.run("Loading pages", this::registerPages);
        StackFormatter.run("Loading items", this::registerItems);
        StackFormatter.run("Loading blocks", this::registerBlocks);
        StackFormatter.run("Loading fluids", this::registerFluids);
        StackFormatter.run("Loading recipe types", this::registerRecipeTypes);
        StackFormatter.run("Loading recipes", this::registerRecipes);
        StackFormatter.run("Loading researches", this::registerResearches);
        if (!suppressLanguageMissingWarning) {
            printMissingLanguage();
        }
        Debug.log("-".repeat(40));
        Debug.log("Registered pack " + getPackID().getId() + ": "
                          + (items == null ? 0 : items.getLoadedItems().get()) + " items, "
                          + (blocks == null ? 0 : blocks.getLoadedBlocks().get()) + " blocks, "
                          + (fluids == null ? 0 : fluids.getLoadedFluids().get()) + " fluids, "
                          + (recipeTypes == null ? 0 : recipeTypes.getLoadedRecipeTypes().get()) + " recipeTypes, "
                          + (recipes == null ? 0 : recipes.getLoadedRecipes().get()) + " recipes, "
                          + (researches == null ? 0 : researches.getLoadedResearches().get()) + " researches, "
                          + (pages == null ? 0 : pages.getLoadedPages().get()) + " pages, "
                          + (saveditems == null ? 0 : saveditems.getItems().size()) + " saveditems."
        );
        Debug.log("-".repeat(40));
        return this;
    }

    public void printMissingLanguage() {
        if (getLanguages() == null || getLanguages().isEmpty()) return;

        File folder = getLangFolder();
        for (Language language : getLanguages()) {
            File lFile = new File(folder, language.localeCode() + ".yml");
            YamlConfiguration config = lFile.exists() ? YamlConfiguration.loadConfiguration(lFile) : new YamlConfiguration();
            checkLanguage(config, language, plugin(), "addon");

            if (items != null) {
                items.getItems().values().forEach(entry -> {
                    checkLanguage(config, language, plugin(), "item." + entry.id().key().getKey() + ".name");
                });
            }
            if (fluids != null) {
                fluids.getFluids().values().forEach(entry -> {
                    checkLanguage(config, language, plugin(), "fluid." + entry.id().key().getKey());
                });
            }
            if (pages != null) {
                pages.getPages().values().forEach(entry -> {
                    checkLanguage(config, language, plugin(), "guide.page." + entry.id().key().getKey());
                });
            }
            if (researches != null) {
                researches.getResearches().values().forEach(entry -> {
                    if (entry.name() == null) {
                        checkLanguage(config, language, plugin(), "research." + entry.id().key().getKey());
                    }
                });
            }
        }
    }

    private void checkLanguage(YamlConfiguration config, Language language, PackAddon addon, String path) {
        if (!config.contains(path)) {
            Debug.warning("Missing language [" + addon.namespace() + "] [" + language.localeCode() + "]: " + path);
        }
    }

    public PackAddon plugin() {
        return getPackNamespace().plugin();
    }
}

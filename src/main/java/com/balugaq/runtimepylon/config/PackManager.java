package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.GlobalVars;
import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.pack.Saveditems;
import com.balugaq.runtimepylon.data.MyArrayList;
import com.balugaq.runtimepylon.exceptions.IdConflictException;
import com.balugaq.runtimepylon.exceptions.PackDependencyMissingException;
import com.balugaq.runtimepylon.exceptions.PluginDependencyMissingException;
import com.balugaq.runtimepylon.exceptions.SaveditemsNotFoundException;
import com.balugaq.runtimepylon.exceptions.UnknownPackException;
import com.balugaq.runtimepylon.exceptions.UnknownSaveditemException;
import com.balugaq.runtimepylon.exceptions.UnsupportedVersionException;
import com.balugaq.runtimepylon.object.CustomGuidePage;
import com.balugaq.runtimepylon.object.PackAddon;
import com.balugaq.runtimepylon.util.Debug;
import com.balugaq.runtimepylon.util.MinecraftVersion;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.guide.button.PageButton;
import io.github.pylonmc.pylon.core.guide.button.ResearchButton;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * We're introducing a new concept: `Pack` `Pack` is a collection set of a configuration to generate customized objects
 * (items/blocks, etc.)
 * <p>
 * In the disk, we have the following tree structure to define a pack:
 * <ul>
 *   <li>packs/
 *     <ul>
 *       <li>pack_id/
 *         <ul>
 *           <li>pack.yml</li>
 *           <li>lang/
 *             <ul>
 *               <li>en.yml</li>
 *               <li>zh-CN.yml</li>
 *             </ul>
 *           </li>
 *           <li>pages/
 *               <ul>
 *                 <li>pages-partA.yml</li>
 *                 <li>pages-partB.yml</li>
 *               </ul>
 *             </li>
 *           <li>items/
 *             <ul>
 *               <li>items-partA.yml</li>
 *               <li>items-partB.yml</li>
 *             </ul>
 *           </li>
 *           <li>blocks/
 *             <ul>
 *               <li>blocks-partA.yml</li>
 *               <li>blocks-partB.yml</li>
 *             </ul>
 *           </li>
 *           <li>fluids/
 *             <ul>
 *               <li>fluids-partA.yml</li>
 *               <li>fluids-partB.yml</li>
 *             </ul>
 *           </li>
 *           <li>recipe_types/
 *               <ul>
 *                   <li>recipe-types-partA.yml</li>
 *                   <li>recipe-types-partB.yml</li>
 *               </ul>
 *           </li>
 *           <li>recipes/
 *             <ul>
 *               <li>minecraft/
 *                 <ul>
 *                   <li>blasting.yml</li>
 *                   <li>campfire_cooking.yml</li>
 *                   <li>smelting.yml</li>
 *                   <li>smoking.yml</li>
 *                   <li>crafting_shaped.yml</li>
 *                   <li>crafting_shapeless.yml</li>
 *                 </ul>
 *               </li>
 *               <li>pylonbase/
 *                 <ul>
 *                   <li>grindstone.yml</li>
 *                   <li>hammer.yml</li>
 *                 </ul>
 *               </li>
 *             </ul>
 *           </li>
 *           <li>settings/
 *             <ul>
 *               <li>ID.yml</li>
 *             </ul>
 *           </li>
 *           <li>scripts/
 *             <ul>
 *                 <li>a.js</li>
 *             </ul>
 *           </li>
 *           <li>researches/
 *             <ul>
 *                 <li>researches-part-A.yml</li>
 *                 <li>researches-part-B.yml</li>
 *             </ul>
 *           </li>
 *         </ul>
 *       </li>
 *     </ul>
 *   </li>
 * </ul>
 * <p>
 * In pack.yml:
 * | Property Type | Property | Description | Pattern | Example |
 * | ------------- | -------- | ----------- | ------- | ------- |
 * | String | id | is the identifier of a pack | `^[A-Za-z0-9_+-]$`| mypack |
 * | String | version | is the version of a pack | `^[A-Za-z0-9_+\-./()]$` | 1.0.0 |
 * | String | *minAPIVersion | defines the minimum API version to run this pack | `[1-9]\d*\.[1-9]\d*(\.[1-9]\d*)?` | 26.1 |
 * | String | *maxAPIVersion | defines the maximum API version to run this pack | `[1-9]\d*\.[1-9]\d*(\.[1-9]\d*)?` | 28.1 |
 * | List<String> | *loadBefores | defines what packs should be loaded before this | `^[A-Za-z0-9_+-]$` | [mypack1, mypack2] |
 * | List<String> | *packDependencies | is the pack dependencies | `^[A-Za-z0-9_+-]$` | [mypack1, mypack2] |
 * | List<String> | *pluginDependencies | is the plugin dependencies | `^[A-Za-z0-9_+-]$` | [plugin1, plugin2] |
 * | String | *author | is the author of a pack | `.*` | balugaq |
 * | List<String> | *authors | is the authors of a pack | `.*` | [balugaq, balugaq2] |https://github.com/balugaq/RuntimePylon/releases
 * | List<String> | *contributors | is the contributors of a pack | `.*` | [balugaq, balugaq2] |
 * | String | *website | is the website of a pack | `^(https?|ftp)://[^\s/$.?#].[^\s]*$` | `https://github.com/balugaq/RuntimePylon` |
 * | String | *githubUpdateLink | is the update link of a pack | `^https?://github\.com/[a-zA-Z0-9_-]+/[a-zA-Z0-9_-]+/releases(/.*)?$` | `https://github.com/balugaq/RuntimePylon/releases` |
 * | List<String> | *languages | defines what languages are supported by this pack | `^[a-z]{2}(-[A-Z]{2})?$` | [en, zh-CN] |
 * | boolean | *suppressLanguageMissingWarning | whether suppress language missing warning or not | `boolean` | false |
 * Properties tagged with * are optional
 * <p>
 * IDs:
 * | ID Type | ID Name | Description | Pattern | It likes |
 * | ------- | ------- | ----------- | ------- | -------- |
 * | String | Pack ID | is the identifier of a pack | `A-Za-z0-9_+-` | Abc |
 * | String | Internal object ID | is the identifier of an object in your own pack | `a-z0-9_-./` | abc |
 * | NamespacedKey | Registered object ID | is the identifier of an object that registered in PylonCore | - | mypack:abc |
 *
 * @author balugaq
 */
@SuppressWarnings("JavadocLinkAsPlainText")
@NullMarked
public @Data class PackManager {
    public static final File PACKS_FOLDER = new File(RuntimePylon.getInstance().getDataFolder(), "packs");
    private static final List<PostLoadTask<? extends PostLoadable>> postLoads = new ObjectArrayList<>(32);
    private final List<Pack> packs = new ArrayList<>();

    @SneakyThrows
    public static void saveConfig(YamlConfiguration config, YamlConfiguration target, File file) {
        for (String key : config.getKeys(true)) {
            if (!target.contains(key)) {
                target.set(key, config.get(key));
            }
        }

        target.save(file);
    }

    public static <T extends PostLoadable> void load(T postLoadable, Consumer<T> consumer) {
        if (postLoadable.postLoad()) postLoads.add(PostLoadTask.of(postLoadable, consumer));
        else consumer.accept(postLoadable);
    }

    public static ItemStack findSaveditem(PackDesc packDesc, SaveditemDesc itemDesc) throws
                                                                                     UnknownPackException, SaveditemsNotFoundException, UnknownSaveditemException {
        Pack pack = packDesc.findPack();
        if (pack == null) throw new UnknownPackException(packDesc);
        Saveditems saveditems = pack.getSaveditems();
        if (saveditems == null) throw new SaveditemsNotFoundException(packDesc);
        ItemStack itemStack = saveditems.find(itemDesc);
        if (itemStack == null) throw new UnknownSaveditemException(packDesc, itemDesc);
        return itemStack;
    }

    public static void packDependencyCycle(List<String> cycle, DependencyType dependencyType) {
        String cycleStr = String.join(" -> ", cycle);
        if (dependencyType == DependencyType.HARD) {
            Debug.severe("Found a pack hard dependency cycle, packs will NOT be loaded: " + cycleStr);
        } else {
            Debug.warn("Found a pack soft dependency cycle, packs will be loaded from " + cycle.stream().min(String::compareTo).get() + ": " + cycleStr);
        }
    }

    @Nullable
    public static Pack findPack(PackDesc desc) {
        return PackManager.getPacks().stream().filter(pack -> pack.getPackID().getId().equals(desc.getId())).findFirst().orElse(null);
    }

    public static List<Pack> getPacks() {
        return RuntimePylon.getPackManager().packs;
    }

    public void loadPacks() {
        if (!PACKS_FOLDER.exists()) PACKS_FOLDER.mkdirs();
        for (File packFolder : PACKS_FOLDER.listFiles()) {
            if (!packFolder.isDirectory()) {
                continue;
            }

            try (var ignored = StackFormatter.setPosition("Loading Pack Folder: " + packFolder.getName())) {
                Debug.log("Loading pack: " + packFolder.getName());
                Pack pack = FileObject.newDeserializer(Pack.class).deserialize(packFolder);
                MinecraftVersion min = pack.getPackMinAPIVersion();
                if (min != null && MinecraftVersion.current().isBefore(min)) {
                    throw new UnsupportedVersionException("Current version is: " + MinecraftVersion.current().humanize() + ", but minimum version to load is: " + min.humanize());
                }
                MinecraftVersion max = pack.getPackMaxAPIVersion();
                if (max != null && MinecraftVersion.current().isAtLeast(max)) {
                    throw new UnsupportedVersionException("Current version is: " + MinecraftVersion.current().humanize() + ", but maximum version to load is: " + max.humanize());
                }
                MyArrayList<PluginDesc> pluginDependencies = pack.getPluginDependencies();
                if (pluginDependencies != null) {
                    ArrayList<PluginDesc> missing = new ArrayList<>();
                    for (PluginDesc pluginDependency : pluginDependencies) {
                        if (!Bukkit.getPluginManager().isPluginEnabled(pluginDependency.getId()))
                            missing.add(pluginDependency);
                    }

                    if (!missing.isEmpty()) throw new PluginDependencyMissingException(pack, missing);
                }
                for (Pack pk : packs) {
                    if (pk.getPackID().equals(pack.getPackID())) {
                        throw new IdConflictException(pk.getPackID(), pk.getDir(), pack.getDir());
                    }
                }
                packs.add(pack);
            } catch (Exception e) {
                StackFormatter.handle(e);
            }
        }

        for (Pack pack : PackSorter.sortPacks(packs)) {
            try (var ignored = StackFormatter.setPosition("Registering Pack: " + pack.getPackID())) {
                MyArrayList<PackDesc> packDependencies = pack.getPackDependencies();
                if (packDependencies != null) {
                    ArrayList<PackDesc> missing = new ArrayList<>();
                    for (PackDesc packDependency : packDependencies) {
                        if (packDependency.findPack() == null) missing.add(packDependency);
                    }

                    if (!missing.isEmpty()) throw new PackDependencyMissingException(pack, missing);
                }
                Debug.log("Registering pack: " + pack.getPackID().getId());
                pack.register();
                Debug.log("Registered pack: " + pack.getPackID().getId());
            } catch (Exception e) {
                StackFormatter.handle(e);
            }
        }

        RuntimePylon.runTaskLater(() -> {
            for (var postLoad : postLoads) {
                postLoad.run();
            }
        }, 1L);
    }

    public void destroy() {
        packs.forEach(PackManager::unload);
        packs.clear();
        GlobalVars.destroy();
    }

    public static void unload(Pack pack) {
        PackAddon plugin = pack.plugin();

        if (pack.getScripts() != null)
            pack.getScripts().closeAll();
        if (pack.getRecipes() != null) {
            for (var e : pack.getRecipes().getRegisteredRecipes().entrySet()) {
                try {
                    var recipes = ReflectionUtil.getValue(e.getKey(), "registeredRecipes", Map.class);
                    if (recipes != null) {
                        for (var key : e.getValue()) {
                            recipes.remove(key);
                        }
                    }
                } catch (IllegalAccessException ex) {
                    StackFormatter.handle(ex);
                }
            }
        }

        try {
            ReflectionUtil.invokeMethod(BlockStorage.INSTANCE, "cleanup$pylon_core", plugin);
        } catch (Exception e) {
            StackFormatter.handle(e);
        }
        try {
            ReflectionUtil.invokeMethod(EntityStorage.INSTANCE, "cleanup$pylon_core", plugin);
        } catch (Exception e) {
            StackFormatter.handle(e);
        }
        RuntimePylon.getGuidePages().values().forEach(page -> page.getButtons().removeIf(item -> {
            return item instanceof PageButton pb && pb.getPage() instanceof CustomGuidePage;
        }));

        RuntimePylon.getGuidePages().values().forEach(page -> page.getButtons().removeIf(item -> {
            if (!(item instanceof ItemButton ib)) {
                return false;
            }

            var pylon = PylonItem.fromStack(ib.getCurrentStack());
            return pylon != null && pylon.getAddon() == plugin;
        }));

        RuntimePylon.getGuidePages().values().forEach(page -> page.getButtons().removeIf(item -> {
            return item instanceof FluidButton fb && fb.getCurrentFluid().getKey().getNamespace().equals(plugin.namespace());
        }));

        RuntimePylon.getGuidePages().values().forEach(page -> page.getButtons().removeIf(item -> {
            return item instanceof ResearchButton rb && rb.getResearch().getKey().getNamespace().equals(plugin.namespace());
        }));

        PylonRegistry.GAMETESTS.unregisterAllFromAddon(plugin);
        PylonRegistry.ITEMS.unregisterAllFromAddon(plugin);
        PylonRegistry.ITEM_TAGS.unregisterAllFromAddon(plugin);
        PylonRegistry.FLUIDS.unregisterAllFromAddon(plugin);
        PylonRegistry.BLOCKS.unregisterAllFromAddon(plugin);
        PylonRegistry.ENTITIES.unregisterAllFromAddon(plugin);
        PylonRegistry.RECIPE_TYPES.unregisterAllFromAddon(plugin);
        PylonRegistry.RESEARCHES.unregisterAllFromAddon(plugin);

        if (PylonRegistry.ADDONS.contains(plugin.getKey())) {
            PylonRegistry.ADDONS.unregister(plugin);
        }
    }
}

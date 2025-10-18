package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.pack.Saveditems;
import com.balugaq.runtimepylon.exceptions.PackDependencyMissingException;
import com.balugaq.runtimepylon.exceptions.PluginDependencyMissingException;
import com.balugaq.runtimepylon.exceptions.SaveditemsNotFoundException;
import com.balugaq.runtimepylon.exceptions.UnknownPackException;
import com.balugaq.runtimepylon.exceptions.UnknownSaveditemException;
import com.balugaq.runtimepylon.exceptions.UnsupportedVersionException;
import com.balugaq.runtimepylon.util.Debug;
import com.balugaq.runtimepylon.util.MinecraftVersion;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * We're introducing a new concept: `Pack`
 * `Pack` is a collection set of a configuration to generate customized objects (items/blocks, etc.)
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
 *               <li>zh_CN.yml</li>
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
 *         </ul>
 *       </li>
 *     </ul>
 *   </li>
 * </ul>
 * <p>
 * In pack.yml:
 * | Property Type | Property | Description | Pattern | Example |
 * | ------------- | -------- | ----------- | ------- | ------- |
 * | String | id | is the identifier of a pack | `A-Za-z0-9_+-`| mypack |
 * | String | version | is the version of a pack | `A-Za-z0-9_+-./()` | 1.0.0 |
 * | String | *minAPIVersion | defines the minimum API version to run this pack | 1.21.3 |
 * | String | *maxAPIVersion | defines the maximum API version to run this pack | 1.21.9 |
 * | List<String> | *loadBefores | defines what packs should be loaded before this | - | [mypack1, mypack2] |
 * | List<String> | *packDependencies | is the pack dependencies | - | [mypack1, mypack2] |
 * | List<String> | *pluginDependencies | is the plugin dependencies | - | [plugin1, plugin2] |
 * | String | *author | is the author of a pack | - | balugaq |
 * | List<String> | *authors | is the authors of a pack | - | [balugaq, balugaq2] |
 * | List<String> | *contributors | is the contributors of a pack | - | [balugaq, balugaq2] |
 * | String | *website | is the website of a pack | - | `https://github.com/balugaq/RuntimePylon` |
 * | String | *githubUpdateLink | is the update link of a pack | - | `https://github.com/balugaq/RuntimePylon/releases` |
 * | List<String> | *languages | defines what languages are supported by this pack | - | [en, zh_CN] |
 * Properties tagged with * are optional
 * <p>
 * IDs:
 * | ID Type | ID Name | Description | Pattern | It likes |
 * | ------- | ------- | ----------- | ------- | -------- |
 * | String | Pack ID | is the identifier of a pack | `A-Za-z0-9_+-` | Abc |
 * | String | Internal object ID | is the identifier of an object in your own pack | `a-z0-9_-./` | abc |
 * | String | External object ID | is the identifier of an object for external packs to access | - | mypack_abc |
 * | NamespacedKey | Registered object ID | is the identifier of an object that registered in PylonCore | - | runtimepylon:mypack_abc |
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

    public void loadPacks() {
        if (!PACKS_FOLDER.exists()) PACKS_FOLDER.mkdirs();
        for (File packFolder : PACKS_FOLDER.listFiles()) {
            if (!packFolder.isDirectory()) {
                continue;
            }

            try (var sk = StackWalker.setPosition("Loading Pack Folder: " + packFolder.getName())) {
                Debug.log("Loading pack: " + packFolder.getName());
                Pack pack = FileObject.newDeserializer(Pack.class).deserialize(packFolder);
                MinecraftVersion min = pack.getPackMinAPIVersion();
                if (min != null && MinecraftVersion.current().isBefore(min)) {
                    throw new UnsupportedVersionException("Current version: " + MinecraftVersion.current() + ", Minimum version to load: " + min);
                }
                MinecraftVersion max = pack.getPackMaxAPIVersion();
                if (max != null && MinecraftVersion.current().isAtLeast(max)) {
                    throw new UnsupportedVersionException("Current version: " + MinecraftVersion.current() + ", Maximum version to load: " + max);
                }
                UnsArrayList<PluginDesc> pluginDependencies = pack.getPluginDependencies();
                if (pluginDependencies != null) {
                    ArrayList<PluginDesc> missing = new ArrayList<>();
                    for (PluginDesc pluginDependency : pluginDependencies) {
                        if (!Bukkit.getPluginManager().isPluginEnabled(pluginDependency.getId()))
                            missing.add(pluginDependency);
                    }

                    if (!missing.isEmpty()) throw new PluginDependencyMissingException(pack, missing);
                }
                packs.add(pack);
                Debug.log("Loaded pack: " + pack.getPackID());
            } catch (Exception e) {
                StackWalker.handle(e);
            }
        }

        for (Pack pack : PackSorter.sortPacks(packs)) {
            try (var sk = StackWalker.setPosition("Registering Pack: " + pack.getPackID())) {
                UnsArrayList<PackDesc> packDependencies = pack.getPackDependencies();
                if (packDependencies != null) {
                    ArrayList<PackDesc> missing = new ArrayList<>();
                    for (PackDesc packDependency : packDependencies) {
                        if (packDependency.findPack() == null) missing.add(packDependency);
                    }

                    if (!missing.isEmpty()) throw new PackDependencyMissingException(pack, missing);
                }
                Debug.log("Registering pack: " + pack.getPackID());
                pack.register();
                Debug.log("Registered pack: " + pack.getPackID());
            } catch (Exception e) {
                StackWalker.handle(e);
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
    }

    public static void unload(Pack pack) {
        PylonAddon plugin = pack.plugin();
        try {
            ReflectionUtil.invokeMethod(BlockStorage.class, "cleanup", plugin);
        } catch (Exception e) {
            StackWalker.handle(e);
        }
        try {
            ReflectionUtil.invokeMethod(EntityStorage.class, "cleanup", plugin);
        } catch (Exception e) {
            StackWalker.handle(e);
        }
        PylonRegistry.GAMETESTS.unregisterAllFromAddon(plugin);
        PylonRegistry.ITEMS.unregisterAllFromAddon(plugin);
        PylonRegistry.ITEM_TAGS.unregisterAllFromAddon(plugin);
        PylonRegistry.FLUIDS.unregisterAllFromAddon(plugin);
        PylonRegistry.BLOCKS.unregisterAllFromAddon(plugin);
        PylonRegistry.ENTITIES.unregisterAllFromAddon(plugin);
        PylonRegistry.RECIPE_TYPES.unregisterAllFromAddon(plugin);
        PylonRegistry.RESEARCHES.unregisterAllFromAddon(plugin);
        PylonRegistry.ADDONS.unregister(plugin);
    }

    public static List<Pack> getPacks() {
        return RuntimePylon.getPackManager().packs;
    }
}

package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.pack.Saveditems;
import com.balugaq.runtimepylon.exceptions.DeserializationException;
import com.balugaq.runtimepylon.exceptions.ExamineFailedException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.exceptions.MissingFileException;
import com.balugaq.runtimepylon.exceptions.SaveditemsNotFoundException;
import com.balugaq.runtimepylon.exceptions.UnknownPackException;
import com.balugaq.runtimepylon.exceptions.UnknownSaveditemException;
import com.balugaq.runtimepylon.util.Debug;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
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
 * | String | PackID | is the identifier of a pack | `A-Za-z0-9_+-`| mypack |
 * | String | PackNamespace | is the namespace of a pack | `a-z0-9_-.` | mypack |
 * | String | PackVersion | is the version of a pack | `A-Za-z0-9_+-./()` | 1.0.0 |
 * | String | *PackMinAPIVersion | defines the minimum API version to run this pack | 1.21.3 |
 * | String | *PackMaxAPIVersion | defines the maximum API version to run this pack | 1.21.9 |
 * | List<String> | *LoadBefores | defines what packs should be loaded before this | - | [mypack1, mypack2] |
 * | List<String> | *PackDependencies | is the pack dependencies | - | [mypack1, mypack2] |
 * | List<String> | *PluginDependencies | is the plugin dependencies | - | [plugin1, plugin2] |
 * | String | *Author | is the author of a pack | - | balugaq |
 * | List<String> | *Authors | is the authors of a pack | - | [balugaq, balugaq2] |
 * | List<String> | *Contributors | is the contributors of a pack | - | [balugaq, balugaq2] |
 * | String | *Website | is the website of a pack | - | `https://github.com/balugaq/RuntimePylon` |
 * | String | *GitHubUpdateLink | is the update link of a pack | - | `https://github.com/balugaq/RuntimePylon/releases` |
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

    /**
     * This method analyzes a given exception and prints out that
     * where the exception occurred: "An exception occurred when <action> at configuration <key> in Pack <packId>"
     *
     * @param e the exception to analyze
     */
    public static void analyze(Exception e) {
        // todo
        Debug.warn(e);
    }

    public void loadPacks() {
        if (!PACKS_FOLDER.exists()) PACKS_FOLDER.mkdirs();
        for (File packFolder : PACKS_FOLDER.listFiles()) {
            if (packFolder.isDirectory()) {
                try {
                    Debug.log("Loading pack: " + packFolder.getName());
                    Pack pack = FileObject.newDeserializer(Pack.class).deserialize(packFolder);
                    packs.add(pack);
                    Debug.log("Loaded pack: " + pack.getPackID());
                } catch (DeserializationException e) {
                    Debug.log("Failed to load pack: " + packFolder + ": " + e.getMessage());
                    analyze(e);
                } catch (MissingFileException e) {
                    Debug.log("Missing file at " + packFolder + ": " + e.getMessage());
                    analyze(e);
                } catch (MissingArgumentException e) {
                    Debug.log("Missing argument at " + packFolder + ": " + e.getMessage());
                    analyze(e);
                } catch (ExamineFailedException e) {
                    Debug.log("Examine failed at " + packFolder + ": " + e.getMessage());
                    analyze(e);
                }
            }
        }

        for (Pack pack : packs) {
            Debug.log("Registering pack: " + pack.getPackID());
            pack.register();
            Debug.log("Registered pack: " + pack.getPackID());
        }

        RuntimePylon.runTaskLater(() -> {
            for (var postLoad : postLoads) {
                postLoad.run();
            }
        }, 1L);
    }

    public void destroy() {
        // todo
    }
}

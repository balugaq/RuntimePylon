package com.balugaq.runtimepylon.config;

import org.jspecify.annotations.NullMarked;

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
 *               <li>en_US.yml</li>
 *               <li>zh_CN.yml</li>
 *             </ul>
 *           </li>
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
 *         </ul>
 *       </li>
 *     </ul>
 *   </li>
 * </ul>
 *
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
 * <p>
 * IDs:
 * | ID Type | ID Name | Description | Pattern | It likes |
 * | ------- | ------- | ----------- | ------- | -------- |
 * | String | Pack ID | is the identifier of a pack | `A-Za-z0-9_+-` | Abc |
 * | String | Internal object ID | is the identifier of an object in your own pack | `a-z0-9_-./` | abc |
 * | String | External object ID | is the identifier of an object for external packs to access | - | mypack:abc |
 * | NamespacedKey | Registered object ID | is the identifier of an object that registered in PylonCore | - | runtimepylon:mypack_abc |
 *
 * @author balugaq
 */
@SuppressWarnings("JavadocLinkAsPlainText")
@NullMarked
public interface IPackManager {
}

package com.balugaq.pc.script.callbacks;

import com.balugaq.pc.GlobalVars;
import com.balugaq.pc.PylonCustomizer;
import com.balugaq.pc.config.Pack;
import com.balugaq.pc.config.PackDesc;
import com.balugaq.pc.manager.PackManager;
import com.caoccao.javet.annotations.V8Function;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.entity.PylonEntitySchema;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.logging.Logger;

/**
 * @author lijinhong11
 * @author balugaq
 */
@NullMarked
public class APICallbacks {
    @V8Function
    public Logger getLogger() {
        return PylonCustomizer.getInstance().getLogger();
    }

    @V8Function
    public PylonRegistry<PylonItemSchema> getItemRegistry() {
        return PylonRegistry.ITEMS;
    }

    @V8Function
    public PylonRegistry<PylonBlockSchema> getBlockRegistry() {
        return PylonRegistry.BLOCKS;
    }

    @V8Function
    public PylonRegistry<PylonEntitySchema> getEntityRegistry() {
        return PylonRegistry.ENTITIES;
    }

    @V8Function
    public PylonRegistry<PylonFluid> getFluidRegistry() {
        return PylonRegistry.FLUIDS;
    }

    @V8Function
    public PylonRegistry<PylonAddon> getAddonRegistry() {
        return PylonRegistry.ADDONS;
    }

    @V8Function
    public NamespacedKey createKey(String namespace, String key) {
        return new NamespacedKey(namespace, key);
    }

    @V8Function
    public void sendMessage(Player player, String message) {
        player.sendMessage(GlobalVars.COMPONENT_SERIALIZER.deserialize(message));
    }

    @V8Function
    @Nullable
    public Pack getPackById(String packId) {
        return PackManager.findPack(new PackDesc(packId));
    }

    @V8Function
    @Nullable
    public ItemStack getSaveditemById(Pack pack, String itemId) {
        return PackManager.getSaveditemById(pack, itemId);
    }
}

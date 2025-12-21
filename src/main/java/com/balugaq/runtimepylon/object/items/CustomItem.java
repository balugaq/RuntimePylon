package com.balugaq.runtimepylon.object.items;

import com.balugaq.runtimepylon.GlobalVars;
import com.balugaq.runtimepylon.object.Scriptable;
import com.balugaq.runtimepylon.util.Debug;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.PylonConfig;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonArmor;
import io.github.pylonmc.pylon.core.item.base.PylonArrow;
import io.github.pylonmc.pylon.core.item.base.PylonBlockInteractor;
import io.github.pylonmc.pylon.core.item.base.PylonBow;
import io.github.pylonmc.pylon.core.item.base.PylonBrewingStandFuel;
import io.github.pylonmc.pylon.core.item.base.PylonBucket;
import io.github.pylonmc.pylon.core.item.base.PylonConsumable;
import io.github.pylonmc.pylon.core.item.base.PylonDispensable;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.item.base.PylonInventoryEffectItem;
import io.github.pylonmc.pylon.core.item.base.PylonInventoryTicker;
import io.github.pylonmc.pylon.core.item.base.PylonItemDamageable;
import io.github.pylonmc.pylon.core.item.base.PylonItemEntityInteractor;
import io.github.pylonmc.pylon.core.item.base.PylonLingeringPotion;
import io.github.pylonmc.pylon.core.item.base.PylonSplashPotion;
import io.github.pylonmc.pylon.core.item.base.PylonTool;
import io.github.pylonmc.pylon.core.item.base.PylonUnmergeable;
import io.github.pylonmc.pylon.core.item.base.PylonWeapon;
import io.github.pylonmc.pylon.core.item.base.VanillaCookingFuel;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.InvocationTargetException;

/**
 * @author balugaq
 */
@NullMarked
public class CustomItem extends PylonItem implements PylonArmor, PylonArrow, PylonBlockInteractor, PylonBow, PylonBrewingStandFuel,
                                                     PylonBucket, PylonConsumable, PylonDispensable, PylonInteractor, PylonInventoryEffectItem,
                                                     PylonInventoryTicker, PylonItemDamageable, PylonItemEntityInteractor, PylonLingeringPotion,
                                                     PylonSplashPotion, PylonTool, PylonUnmergeable, PylonWeapon, VanillaCookingFuel, Scriptable {
    public CustomItem(final ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToClickBlock(final PlayerInteractEvent event) {
        callScript(this, event);
    }

    @Override
    public void onUsedAsBrewingStandFuel(final BrewingStandFuelEvent event) {
        callOrCancelEvent(this, event);
    }

    @Override
    public void onConsumed(final PlayerItemConsumeEvent event) {
        callOrCancelEvent(this, event);
    }

    @Override
    public void onUsedToRightClick(final PlayerInteractEvent event) {
        callScript(this, event);
    }

    @Override
    public void onUsedToRightClickEntity(final PlayerInteractEntityEvent event) {
        callScript(this, event);
    }

    @Override
    public void onDispense(final BlockDispenseEvent event) {
        callScript(this, event);
    }

    @Override
    public long getTickInterval() {
        var settings = getSettingsOrNull();
        if (settings == null) return PylonConfig.getDefaultTickInterval();
        return settings.get("tick-interval", ConfigAdapter.LONG, (long) PylonConfig.getDefaultTickInterval());
    }

    @Override
    public void onSplash(final LingeringPotionSplashEvent event) {
        callOrCancelEvent(this, event);
    }

    @Override
    public void onSplash(final PotionSplashEvent event) {
        callOrCancelEvent(this, event);
    }

    @Override
    public void onBurntAsFuel(final FurnaceBurnEvent event) {
        callOrCancelEvent(this, event);
    }

    @Override
    public void onArrowReady(final PlayerReadyArrowEvent event) {
        callOrCancelEvent(this, event);
    }

    @Override
    public void onArrowShotFromBow(final EntityShootBowEvent event) {
        callOrCancelEvent(this, event);
    }

    @Override
    public void onArrowHit(final ProjectileHitEvent event) {
        callOrCancelEvent(this, event);
    }

    @Override
    public void onArrowDamage(final EntityDamageByEntityEvent event) {
        callScript(this, event);
    }

    @Override
    public void onBowReady(final PlayerReadyArrowEvent event) {
        callOrCancelEvent(this, event);
    }

    @Override
    public void onBowFired(final EntityShootBowEvent event) {
        callOrCancelEvent(this, event);
    }

    @Override
    public void onBucketEmptied(final PlayerBucketEmptyEvent event) {
        callOrCancelEvent(this, event);
    }

    @Override
    public void onBucketFilled(final PlayerBucketFillEvent event) {
        callOrCancelEvent(this, event);
    }

    @Override
    public boolean respectCooldown() {
        var settings = getSettingsOrNull();
        if (settings == null) return true;
        return settings.get("respect-cooldown", ConfigAdapter.BOOLEAN, true);
    }

    @Override
    public NamespacedKey getItemKey() {
        return PylonInventoryEffectItem.super.getItemKey();
    }

    @Override
    public void onTick(final Player player) {
        PylonInventoryEffectItem.super.onTick(player);
        callScript(this, player);
    }

    @Override
    public void onRemovedFromInventory(final Player player) {
        PylonInventoryEffectItem.super.onRemovedFromInventory(player);
        callScript(this, player);
    }

    @Override
    public void onAddedToInventory(final Player player) {
        PylonInventoryEffectItem.super.onAddedToInventory(player);
        callScript(this, player);
    }

    @Override
    public void onItemDamaged(final PlayerItemDamageEvent event) {
        callScript(this, event);
    }

    @Override
    public void onItemBreaks(final PlayerItemBreakEvent event) {
        callScript(this, event);
    }

    @Override
    public void onItemMended(final PlayerItemMendEvent event) {
        callScript(this, event);
    }

    @Override
    public void onUsedToDamageBlock(final BlockDamageEvent event) {
        callScript(this, event);
    }

    @Override
    public void onUsedToBreakBlock(final BlockBreakEvent event) {
        callScript(this, event);
    }

    @Override
    public void onUsedToDamageEntity(final EntityDamageByEntityEvent event) {
        callScript(this, event);
    }

    @Override
    public void onUsedToKillEntity(final EntityDeathEvent event) {
        callScript(this, event);
    }

    @Override
    public Key getEquipmentType() {
        return GlobalVars.getEquipmentType(getKey());
    }

    @Nullable
    public Config getSettingsOrNull() {
        try {
            return (Config) ReflectionUtil.invokeMethod(PylonUtils.class, "mergeGlobalConfig", PylonUtils.getAddon(getKey()), "settings/" + getKey().getKey() + ".yml", "settings/" + getKey().getNamespace() + "/" + getKey().getKey() + ".yml", false);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Debug.warn(e);
            return null;
        }
    }
}

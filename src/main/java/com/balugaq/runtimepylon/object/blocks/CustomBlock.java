package com.balugaq.runtimepylon.object.blocks;

import com.balugaq.runtimepylon.GlobalVars;
import com.balugaq.runtimepylon.object.Scriptable;
import com.balugaq.runtimepylon.util.Debug;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonBeacon;
import io.github.pylonmc.pylon.core.block.base.PylonBell;
import io.github.pylonmc.pylon.core.block.base.PylonBreakHandler;
import io.github.pylonmc.pylon.core.block.base.PylonCampfire;
import io.github.pylonmc.pylon.core.block.base.PylonCauldron;
import io.github.pylonmc.pylon.core.block.base.PylonComposter;
import io.github.pylonmc.pylon.core.block.base.PylonFlowerPot;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGrowable;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonJumpBlock;
import io.github.pylonmc.pylon.core.block.base.PylonLeaf;
import io.github.pylonmc.pylon.core.block.base.PylonLectern;
import io.github.pylonmc.pylon.core.block.base.PylonNoVanillaContainerBlock;
import io.github.pylonmc.pylon.core.block.base.PylonNoteBlock;
import io.github.pylonmc.pylon.core.block.base.PylonPiston;
import io.github.pylonmc.pylon.core.block.base.PylonRedstoneBlock;
import io.github.pylonmc.pylon.core.block.base.PylonShearable;
import io.github.pylonmc.pylon.core.block.base.PylonSign;
import io.github.pylonmc.pylon.core.block.base.PylonSneakableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSponge;
import io.github.pylonmc.pylon.core.block.base.PylonTNT;
import io.github.pylonmc.pylon.core.block.base.PylonTargetBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTrialVault;
import io.github.pylonmc.pylon.core.block.base.PylonUnloadBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.PylonConfig;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.event.PylonBlockUnloadEvent;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.papermc.paper.event.block.BeaconActivatedEvent;
import io.papermc.paper.event.block.BeaconDeactivatedEvent;
import io.papermc.paper.event.block.CompostItemEvent;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import io.papermc.paper.event.block.TargetHitEvent;
import io.papermc.paper.event.entity.EntityCompostItemEvent;
import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent;
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import io.papermc.paper.event.player.PlayerInsertLecternBookEvent;
import io.papermc.paper.event.player.PlayerLecternPageChangeEvent;
import io.papermc.paper.event.player.PlayerOpenSignEvent;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BellResonateEvent;
import org.bukkit.event.block.BellRingEvent;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.InventoryBlockStartEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.block.VaultDisplayItemEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public class CustomBlock extends PylonBlock implements PylonInteractBlock, PylonTickingBlock, PylonNoVanillaContainerBlock,
                                                       PylonBeacon, PylonBell, PylonBreakHandler, PylonCampfire, PylonCauldron,
                                                       PylonComposter, PylonFlowerPot, PylonFluidBlock, PylonFluidBufferBlock,
                                                       PylonGrowable, PylonJumpBlock, PylonLeaf, PylonLectern, PylonNoteBlock,
                                                       PylonPiston, PylonRedstoneBlock, PylonShearable, PylonSign, PylonSneakableBlock,
                                                       PylonSponge, PylonTargetBlock, PylonTNT, PylonTrialVault, PylonUnloadBlock,
                                                       PylonGuiBlock, Scriptable {
    public CustomBlock(final Block block) {
        super(block);
    }

    public CustomBlock(final Block block, final PersistentDataContainer pdc) {
        super(block, pdc);
    }

    public CustomBlock(final Block block, final BlockCreateContext context) {
        super(block, context);
        for (var e : GlobalVars.getFluidBlockData(getKey())) {
            createFluidPoint(e.fluidPointType(), e.face(), context, e.allowVerticalFaces());
        }
        for (var e : GlobalVars.getFluidBufferBlockData(getKey())) {
            createFluidBuffer(e.fluid(), e.capacity(), e.input(), e.output());
        }
    }

    @Override
    public void onInteract(final PlayerInteractEvent event) {
        callScriptA("onPreInteract", this, event);

        if (!event.getAction().isRightClick()
                || event.getPlayer().isSneaking()
                || event.getHand() != EquipmentSlot.HAND
                || event.useInteractedBlock() == Event.Result.DENY) {
            return;
        }

        if (GlobalVars.getGui(getKey()) != GlobalVars.PLACEHOLDER_GUI) {
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
            Window.single()
                    .setGui(getGui())
                    .setTitle(new AdventureComponentWrapper(getGuiTitle()))
                    .setViewer(event.getPlayer())
                    .build()
                    .open();
        }

        callScriptA("onPostInteract", this, event);
    }

    @Override
    public void onFlowerPotManipulated(final PlayerFlowerPotManipulateEvent event) {
        callScript(this, event);
    }

    @Override
    public void onActivated(final BeaconActivatedEvent event) {
        callScript(this, event);
    }

    @Override
    public void onDeactivated(final BeaconDeactivatedEvent event) {
        callScript(this, event);
    }

    @Override
    public void onEffectChange(final PlayerChangeBeaconEffectEvent event) {
        callScript(this, event);
    }

    @Override
    public void onEffectApply(final BeaconEffectEvent event) {
        callScript(this, event);
    }

    @Override
    public void onRing(final BellRingEvent event) {
        callScript(this, event);
    }

    @Override
    public void onResonate(final BellResonateEvent event) {
        callScript(this, event);
    }

    @Override
    public boolean preBreak(final BlockBreakContext context) {
        var v = callScript(this, context);
        if (v == null) return true;
        return Boolean.TRUE.equals(v);
    }

    @Override
    public void onBreak(final List<ItemStack> drops, final BlockBreakContext context) {
        callScript(this, drops, context);
    }

    @Override
    public void postBreak(final BlockBreakContext context) {
        callScript(this, context);
    }

    @Override
    public void onStartCooking(final InventoryBlockStartEvent event) {
        callScript(this, event);
    }

    @Override
    public void onEndCooking(final BlockCookEvent event) {
        callScript(this, event);
    }

    @Override
    public void onLevelChange(final CauldronLevelChangeEvent event) {
        callScript(this, event);
    }

    @Override
    public void onCompostByHopper(final CompostItemEvent event) {
        callScript(this, event);
    }

    @Override
    public void onCompostByEntity(final EntityCompostItemEvent event) {
        callScript(this, event);
    }

    @Override
    public void onInventoryOpen(final InventoryOpenEvent event) {
        callScript(this, event);
    }

    @Override
    public void onItemMoveTo(final InventoryMoveItemEvent event) {
        callScript(this, event);
    }

    @Override
    public void onItemMoveFrom(final InventoryMoveItemEvent event) {
        callScript(this, event);
    }

    @Override
    public void onDecayNaturally(final LeavesDecayEvent event) {
        callScript(this, event);
    }

    @Override
    public void onJumpedOn(final PlayerJumpEvent event) {
        callScript(this, event);
    }

    @Override
    public void onGrow(final BlockGrowEvent event) {
        callScript(this, event);
    }

    @Override
    public void onFertilize(final BlockFertilizeEvent event) {
        callScript(this, event);
    }

    @Override
    public void onInsertBook(final PlayerInsertLecternBookEvent event) {
        callScript(this, event);
    }

    @Override
    public void onRemoveBook(final PlayerTakeLecternBookEvent event) {
        callScript(this, event);
    }

    @Override
    public void onChangePage(final PlayerLecternPageChangeEvent event) {
        callScript(this, event);
    }

    @Override
    public int getTickInterval() {
        if (!isFunctionExists("tick"))
            return Integer.MAX_VALUE;
        var settings = getSettingsOrNull();
        if (settings == null) return PylonConfig.getDefaultTickInterval();
        return settings.get("tick-interval", ConfigAdapter.INT, PylonConfig.getDefaultTickInterval());
    }

    @Override
    public boolean isAsync() {
        var settings = getSettingsOrNull();
        if (settings == null) return false;
        return settings.get("async", ConfigAdapter.BOOLEAN, false);
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

    @Override
    public void tick(final double deltaSeconds) {
        callScript(this, deltaSeconds);
    }

    @Override
    public void onNotePlay(final NotePlayEvent event) {
        callScript(this, event);
    }

    @Override
    public void onCurrentChange(final BlockRedstoneEvent event) {
        callScript(this, event);
    }

    @Override
    public void onShear(final PlayerShearBlockEvent event) {
        callScript(this, event);
    }

    @Override
    public void onAbsorb(final SpongeAbsorbEvent event) {
        callScript(this, event);
    }

    @Override
    public void onIgnite(final TNTPrimeEvent event) {
        callScript(this, event);
    }

    @Override
    public void onHit(final TargetHitEvent event) {
        callScript(this, event);
    }

    @Override
    public void onDisplayItem(final VaultDisplayItemEvent event) {
        callScript(this, event);
    }

    @Override
    public void onUnload(final PylonBlockUnloadEvent event) {
        callScript(this, event);
    }

    @Override
    public void onExtend(final BlockPistonExtendEvent event) {
        callScript(this, event);
    }

    @Override
    public void onRetract(final BlockPistonRetractEvent event) {
        callScript(this, event);
    }

    @Override
    public void onSignChange(final SignChangeEvent event) {
        callScript(this, event);
    }

    @Override
    public void onOpen(final PlayerOpenSignEvent event) {
        callScript(this, event);
    }

    @Override
    public void onSneakedOn(final PlayerToggleSneakEvent event) {
        callScript(this, event);
    }

    @Override
    public void onUnsneakedOn(final PlayerToggleSneakEvent event) {
        callScript(this, event);
    }

    @Override
    public Gui createGui() {
        return GlobalVars.getGui(getKey());
    }
}

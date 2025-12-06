package com.balugaq.runtimepylon.object.blocks;

import com.balugaq.runtimepylon.GlobalVars;
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
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.event.PylonBlockUnloadEvent;
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
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

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
                                                       PylonGuiBlock {
    public final Config settings = Settings.get(getKey());

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
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onPreInteract",
                this, event
        ));

        if (!event.getAction().isRightClick()
                || event.getPlayer().isSneaking()
                || event.getHand() != EquipmentSlot.HAND
                || event.useInteractedBlock() == Event.Result.DENY) {
            return;
        }

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        Window.single()
                .setGui(getGui())
                .setTitle(new AdventureComponentWrapper(getGuiTitle()))
                .setViewer(event.getPlayer())
                .build()
                .open();

        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onPostInteract",
                this, event
        ));
    }

    @Override
    public void tick(final double deltaSeconds) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "tick",
                this, deltaSeconds
        ));
    }

    @Override
    public void onFlowerPotManipulated(final PlayerFlowerPotManipulateEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onFlowerPotManipulated",
                this, event
        ));
    }

    @Override
    public void onActivated(final BeaconActivatedEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onActivated",
                this, event
        ));
    }

    @Override
    public void onDeactivated(final BeaconDeactivatedEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onDeactivated",
                this, event
        ));
    }

    @Override
    public void onEffectChange(final PlayerChangeBeaconEffectEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onEffectChange",
                this, event
        ));
    }

    @Override
    public void onEffectApply(final BeaconEffectEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onEffectApply",
                this, event
        ));
    }

    @Override
    public void onResonate(final BellResonateEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onResonate",
                this, event
        ));
    }

    @Override
    public void onRing(final BellRingEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onRing",
                this, event
        ));
    }

    @Override
    public boolean preBreak(final BlockBreakContext context) {
        var opt = GlobalVars.getScriptO(getKey());
        if (opt.isPresent()) {
            return Boolean.TRUE.equals(opt.get().executeFunction(
                    "preBreak",
                    this, context
            ).get());
        }
        return true;
    }

    @Override
    public void onBreak(final List<ItemStack> drops, final BlockBreakContext context) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onBreak",
                this, drops, context
        ));
    }

    @Override
    public void postBreak(final BlockBreakContext context) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "postBreak",
                this, context
        ));
    }

    @Override
    public void onStartCooking(final InventoryBlockStartEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onStartCooking",
                this, event
        ));
    }

    @Override
    public void onEndCooking(final BlockCookEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onEndCooking",
                this, event
        ));
    }

    @Override
    public void onLevelChange(final CauldronLevelChangeEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onLevelChange",
                this, event
        ));
    }

    @Override
    public void onCompostByHopper(final CompostItemEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onCompostByHopper",
                this, event
        ));
    }

    @Override
    public void onCompostByEntity(final EntityCompostItemEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onCompostByEntity",
                this, event
        ));
    }

    @Override
    public void onInventoryOpen(final InventoryOpenEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onInventoryOpen",
                this, event
        ));
    }

    @Override
    public void onItemMoveTo(final InventoryMoveItemEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onItemMoveTo",
                this, event
        ));
    }

    @Override
    public void onItemMoveFrom(final InventoryMoveItemEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onItemMoveFrom",
                this, event
        ));
    }

    @Override
    public void onDecayNaturally(final LeavesDecayEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onDecayNaturally",
                this, event
        ));
    }

    @Override
    public void onJumpedOn(final PlayerJumpEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onJumpedOn",
                this, event
        ));
    }

    @Override
    public void onFertilize(final BlockFertilizeEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onFertilize",
                this, event
        ));
    }

    @Override
    public void onGrow(final BlockGrowEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onGrow",
                this, event
        ));
    }

    @Override
    public void onChangePage(final PlayerLecternPageChangeEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onChangePage",
                this, event
        ));
    }

    @Override
    public void onInsertBook(final PlayerInsertLecternBookEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onInsertBook",
                this, event
        ));
    }

    @Override
    public void onRemoveBook(final PlayerTakeLecternBookEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onRemoveBook",
                this, event
        ));
    }

    @Override
    public boolean isAsync() {
        return settings.get("async", ConfigAdapter.BOOLEAN, false);
    }

    @Override
    public int getTickInterval() {
        return settings.get("tick-interval", ConfigAdapter.INT, PylonConfig.getDefaultTickInterval());
    }

    @Override
    public void onNotePlay(@NotNull final NotePlayEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onNotePlay",
                this, event
        ));
    }

    @Override
    public void onCurrentChange(@NotNull final BlockRedstoneEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onCurrentChange",
                this, event
        ));
    }

    @Override
    public void onShear(@NotNull final PlayerShearBlockEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onShear",
                this, event
        ));
    }

    @Override
    public void onAbsorb(@NotNull final SpongeAbsorbEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onAbsorb",
                this, event
        ));
    }

    @Override
    public void onIgnite(@NotNull final TNTPrimeEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onIgnite",
                this, event
        ));
    }

    @Override
    public void onHit(@NotNull final TargetHitEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onHit",
                this, event
        ));
    }

    @Override
    public void onDisplayItem(@NotNull final VaultDisplayItemEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onDisplayItem",
                this, event
        ));
    }

    @Override
    public void onUnload(@NotNull final PylonBlockUnloadEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onUnload",
                this, event
        ));
    }

    @Override
    public void onExtend(@NotNull final BlockPistonExtendEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onExtend",
                this, event
        ));
    }

    @Override
    public void onRetract(@NotNull final BlockPistonRetractEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onRetract",
                this, event
        ));
    }

    @Override
    public void onOpen(@NotNull final PlayerOpenSignEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onOpen",
                this, event
        ));
    }

    @Override
    public void onSignChange(@NotNull final SignChangeEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onSignChange",
                this, event
        ));
    }

    @Override
    public void onSneakedOn(@NotNull final PlayerToggleSneakEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onSneakedOn",
                this, event
        ));
    }

    @Override
    public void onUnsneakedOn(@NotNull final PlayerToggleSneakEvent event) {
        GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                "onUnsneakedOn",
                this, event
        ));
    }

    @Override
    public @NotNull Gui createGui() {
        return GlobalVars.getGui(getKey());
    }
}

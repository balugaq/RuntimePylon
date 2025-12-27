package com.balugaq.runtimepylon.object.blocks;

import com.balugaq.runtimepylon.GlobalVars;
import com.balugaq.runtimepylon.config.FluidBlockData;
import com.balugaq.runtimepylon.config.FluidBufferBlockData;
import com.balugaq.runtimepylon.config.GuiData;
import com.balugaq.runtimepylon.config.LogisticBlockData;
import com.balugaq.runtimepylon.object.CustomRecipe;
import com.balugaq.runtimepylon.object.CustomRecipeType;
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
import io.github.pylonmc.pylon.core.block.base.PylonLogisticBlock;
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
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
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
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
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
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.window.Window;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
                                                       PylonGuiBlock, PylonLogisticBlock, Scriptable {
    private final Char2ObjectOpenHashMap<VirtualInventory> vis = new Char2ObjectOpenHashMap<>();
    private final @Nullable RecipeType<?> loadRecipeType = GlobalVars.getLoadRecipeType(getKey());
    private final @Nullable GuiData guiData = GlobalVars.getGuiData(getKey());
    private final @Nullable LogisticBlockData logisticBlockData = GlobalVars.getLogisticBlockData(getKey());
    private final @Nullable FluidBlockData fluidBlockData = GlobalVars.getFluidBlockData(getKey());
    private final @Nullable FluidBufferBlockData fluidBufferBlockData = GlobalVars.getFluidBufferBlockData(getKey());
    private @Nullable CustomRecipe processingRecipe = null;
    private int remainingTicks;

    public CustomBlock(final Block block) {
        super(block);
    }

    public CustomBlock(final Block block, final PersistentDataContainer pdc) {
        super(block, pdc);
    }

    public CustomBlock(final Block block, final BlockCreateContext context) {
        super(block, context);
        for (var e : fluidBlockData) {
            createFluidPoint(e.fluidPointType(), e.face(), context, e.allowVerticalFaces());
        }
        if (fluidBufferBlockData != null) {
            for (var e : fluidBufferBlockData) {
                createFluidBuffer(e.fluid(), e.capacity(), e.input(), e.output());
            }
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

        if (guiData != null) {
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
        if (v instanceof Boolean b) {
            return b;
        }
        return false;
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
        if (processingRecipe != null) {
            remainingTicks--;
            if (remainingTicks <= 0) {
                // push item or fluid
                for (var e : processingRecipe.getResults()) {
                    if (e instanceof FluidOrItem.Item item) {
                        var vo = vis.get('o');
                        vo.addItem(UpdateReason.SUPPRESSED, item.item());
                    }
                    if (e instanceof FluidOrItem.Fluid fluid) {
                        setFluid(fluid.fluid(), Math.min(fluidCapacity(fluid.fluid()), fluidAmount(fluid.fluid()) + fluid.amountMillibuckets()));
                    }
                }
            } else {
                return;
            }
        }

        if (loadRecipeType != null) {
            Collection<CustomRecipe> recipes = (Collection<CustomRecipe>) loadRecipeType.getRecipes();
            @Nullable var vi = vis.get('i');
            @Nullable var vo = vis.get('o');
            recipe: for (CustomRecipe recipe : recipes) {
                for (var e : recipe.getInputs()) {
                    switch (e) {
                        case RecipeInput.Item item -> {
                            if (logisticBlockData == null || vi == null) continue recipe;
                            if (!vi.contains(item::contains)) continue recipe;
                            if (vi.count(item::contains) < item.getAmount()) continue recipe;
                        }
                        case RecipeInput.Fluid fluid -> {
                            if (fluidBufferBlockData == null) continue recipe;
                            boolean enough = false;
                            for (var f : fluid.fluids()) {
                                if (hasFluid(f) && fluidAmount(f) >= fluid.amountMillibuckets() && fluidBufferBlockData.inputFluids().contains(f)) {
                                    enough = true;
                                    break;
                                }
                            }
                            if (!enough) continue recipe;
                        }
                        default -> {
                            continue recipe;
                        }
                    }
                }

                var ret = countResults(recipe);
                if (!ret.isEmpty() && (vo == null || (vo != null && logisticBlockData != null && !canOutputItems(ret, vo)))) continue recipe;
                if (fluidBufferBlockData != null && !canOutputFluid(countOutputFluids(recipe), fluidBufferBlockData)) continue recipe;

                if (!recipe.getOther().isEmpty()) {
                    if (!handleRecipeOther(this, recipe, logisticBlockData, fluidBufferBlockData, loadRecipeType)) continue recipe;
                }

                // found recipe
                processingRecipe = recipe;
                remainingTicks = recipe.getTimeTicks();

                // consume items and fluids
                for (var e : recipe.getInputs()) {
                    if (e instanceof RecipeInput.Item item) {
                        int remainToConsume = item.getAmount();
                        for (int i = 0; i < vi.getSize(); i++) {
                            ItemStack stack = vi.getItem(i);
                            if (stack == null || stack.getType().isAir()) continue;
                            if (item.matches(stack)) {
                                int consume = Math.min(remainToConsume, stack.getAmount());
                                vi.setItemAmount(UpdateReason.SUPPRESSED, i, stack.getAmount() - consume);
                                remainToConsume -= consume;
                            }
                        }
                    }
                    if (e instanceof RecipeInput.Fluid fluid) {
                        for (var f : fluid.fluids()) {
                            if (hasFluid(f) && fluidAmount(f) >= fluid.amountMillibuckets() && fluidBufferBlockData.inputFluids().contains(f)) {
                                removeFluid(f, fluid.amountMillibuckets());
                            }
                        }
                    }
                }
            }
        }
        callScript(this, deltaSeconds);
    }

    public boolean handleRecipeOther(@Nullable Object... args) {
        var v = callScript(this, args);
        if (v instanceof Boolean b) {
            return b;
        }
        return false;
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
        return CustomRecipeType.makeGui(guiData);
    }

    @Override
    public void setupLogisticGroups() {
        if (logisticBlockData != null) {
            for (var e : logisticBlockData) {
                var vi = vis.get(e.invSlotChar());
                if (vi == null) {
                    continue;
                }
                createLogisticGroup(e.name(), e.slotType(), vi);
            }
        }
    }

    public boolean canOutputFluid(Map<PylonFluid, Double> results, FluidBufferBlockData fluidBufferBlockData) {
        for (var e : results.entrySet()) {
            if (!hasFluid(e.getKey())
            || !fluidBufferBlockData.outputFluids().contains(e.getKey())
            || fluidAmount(e.getKey()) + e.getValue() > fluidCapacity(e.getKey())) {
                return false;
            }
        }
        return true;
    }

    public boolean canOutputItems(Object2IntLinkedOpenHashMap<ItemStack> results, VirtualInventory inventory) {
        int[] r = inventory.simulateAdd(results.sequencedKeySet().stream().toList());
        int[] target = results.sequencedValues().stream().mapToInt(i -> i).toArray();

        for (int i = 0; i < r.length; i++) {
            if (r[i] < target[i]) {
                return false;
            }
        }

        return true;
    }

    public static Object2DoubleOpenHashMap<PylonFluid> countOutputFluids(PylonRecipe recipe) {
        if (recipe instanceof CustomRecipe cr && cr.getCountOutputFluids() != null) {
            return cr.getCountOutputFluids();
        }

        var ret = new Object2DoubleOpenHashMap<PylonFluid>();
        for (var e : recipe.getResults()) {
            if (e instanceof FluidOrItem.Fluid fluid) {
                ret.addTo(fluid.fluid(), fluid.amountMillibuckets());
            }
        }

        if (recipe instanceof CustomRecipe cr) {
            cr.setCountOutputFluids(ret);
        }

        return ret;
    }

    public static Object2IntLinkedOpenHashMap<ItemStack> countResults(PylonRecipe recipe) {
        if (recipe instanceof CustomRecipe cr && cr.getCountOutputItems() != null) {
            return cr.getCountOutputItems();
        }

        var ret = new Object2IntLinkedOpenHashMap<ItemStack>();
        for (var e : recipe.getResults()) {
            if (e instanceof FluidOrItem.Item item) {
                var it = item.item();
                ret.addTo(it.asOne(), it.getAmount());
            }
        }

        if (recipe instanceof CustomRecipe cr) {
            cr.setCountOutputItems(ret);
        }

        return ret;
    }
}

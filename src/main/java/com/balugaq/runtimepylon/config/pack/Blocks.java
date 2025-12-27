package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.GlobalVars;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.FluidBlockData;
import com.balugaq.runtimepylon.config.FluidBufferBlockData;
import com.balugaq.runtimepylon.config.GuiData;
import com.balugaq.runtimepylon.config.GuiReader;
import com.balugaq.runtimepylon.config.InternalObjectID;
import com.balugaq.runtimepylon.config.LogisticBlockData;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.RecipeTypeDesc;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.config.SingletonFluidBlockData;
import com.balugaq.runtimepylon.config.SingletonFluidBufferBlockData;
import com.balugaq.runtimepylon.config.SingletonLogisticBlockData;
import com.balugaq.runtimepylon.config.StackFormatter;
import com.balugaq.runtimepylon.config.preloads.PreparedBlock;
import com.balugaq.runtimepylon.config.register.PreRegister;
import com.balugaq.runtimepylon.exceptions.IncompatibleKeyFormatException;
import com.balugaq.runtimepylon.exceptions.IncompatibleMaterialException;
import com.balugaq.runtimepylon.exceptions.InvalidMultiblockComponentException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.exceptions.UnknownRecipeTypeException;
import com.balugaq.runtimepylon.exceptions.UnknownSymbolException;
import com.balugaq.runtimepylon.util.MaterialUtil;
import com.balugaq.runtimepylon.util.StringUtil;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.gui.Gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <li>blocks/
 *   <ul>
 *     <li>blocks-partA.yml</li>
 *     <li>blocks-partB.yml</li>
 *   </ul>
 * </li>
 * <p>
 * For each yml:
 * <p>
 * [Internal object ID]:
 *   material: [Material Format]
 *   *script: [ScriptDesc]
 *   *postload: boolean
 *   *gui:
 *     structure: |-
 *       B B B B B B B B B
 *       I a b I B O 1 2 O
 *       I c d I B O 3 4 O
 *       I e f I B O 5 6 O
 *       B B B B B B B B B
 *     [char]: [Material Format]
 *   *fluid-block:
 *     1: [SingletonFluidBlockData]
 *     2: [SingletonFluidBlockData]
 *   *fluid-buffer:
 *     1: [SingletonFluidBufferBlockData]
 *     2: [SingletonFluidBufferBlockData]
 *   *multiblock:
 *     positions:
 *       "1;0;0": [Multiblock Component Symbol]
 *       "-1;0;0": [Multiblock Component Symbol]
 *       "0;1;0": [Multiblock Component Symbol]
 *       "0;-1;0": [Multiblock Component Symbol]
 *     blocks:
 *       [Multiblock Component Symbol]: [Multiblock Component Desc]
 *   *logistic:
 *     *input: [SingleLogisticBlockData]
 *     *output: [SingleLogisticBlockData]
 *
 * <p>
 * [SingletonFluidBlockData]:
 *   type: [FluidPointType]
 *   face: [Cartesian BlockFace]
 *   *allow-vertical-faces: boolean # (true by default)
 * <p>
 * [SingletonFluidBufferBlockData]:
 *   fluid: [PylonFluid]
 *   capacity: double
 *   input: boolean # (false by default)
 *   output: boolean # (false by default)
 * <p>
 * [FluidPointType]: input | output | intersection
 * [Cartesian BlockFace]: up | down | north | south | east | west
 * <p>
 * [Multiblock Component Desc]:
 * pylonbase:tin_block
 * minecraft:iron_block
 * minecraft:fire[lit=true] | minecraft:soul_fire[lit=true]
 * <p>
 * [SingleLogisticBlockData]:
 *   name: [String]
 *   inv-slot-char: [char]
 *
 * @author balugaq
 */
@Data
@NullMarked
public class Blocks implements FileObject<Blocks> {
    private AtomicInteger loadedBlocks = new AtomicInteger(0);
    private PackNamespace namespace;
    private Map<RegisteredObjectID, PreparedBlock> blocks = new HashMap<>();

    public Blocks setPackNamespace(PackNamespace namespace) {
        this.namespace = namespace;
        return this;
    }

    // @formatter:off
    @Override
    public List<FileReader<Blocks>> readers() {
        return List.of(dir -> {
            List<File> files = Arrays.stream(dir.listFiles()).toList();
            List<File> ymls = files.stream().filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")).toList();

            for (File yml : ymls) {try (var ignored = StackFormatter.setPosition("Reading file: " + StringUtil.simplifyPath(yml.getAbsolutePath()))) {
                var config = YamlConfiguration.loadConfiguration(yml);

                for (String key : config.getKeys(false)) {try (var ignored1 = StackFormatter.setPosition("Reading key: " + key)) {
                    var section = PreRegister.read(config, key);
                    if (section == null) continue;

                    if (!section.contains("material")) throw new MissingArgumentException("material");

                    var s2 = section.get("material");

                    ItemStack item = Deserializer.ITEMSTACK.deserialize(s2);
                    if (item == null) continue;
                    Material dm = MaterialUtil.getDisplayMaterial(item);
                    if (!dm.isBlock() || dm.isAir()) throw new IncompatibleMaterialException("material must be blocks: " + item.getType());

                    var id = InternalObjectID.of(key).register(namespace);
                    boolean postLoad = section.getBoolean("postload", false);
                    blocks.put(id, new PreparedBlock(id, dm, postLoad));

                    // Global var loads
                    // script
                    ScriptDesc scriptdesc = Pack.readOrNull(section, ScriptDesc.class, "script");
                    namespace.registerScript(id, scriptdesc);

                    // fluid-block
                    var fluidBlock = section.getConfigurationSection("fluid-block");
                    if (fluidBlock != null) {
                        try (var ignored2 = StackFormatter.setPosition("Reading fluid-block section: " + key)) {

                        List<SingletonFluidBlockData> singletons = new ArrayList<>();
                        for (String k : fluidBlock.getKeys(false)) {
                            var singleton = Pack.read(fluidBlock, SingletonFluidBlockData.class, k);
                            singletons.add(singleton);
                        }
                        GlobalVars.putFluidBlockData(id.key(), new FluidBlockData(singletons));

                        } catch (Exception ex) {
                            StackFormatter.handle(ex);
                        }
                    }

                    // fluid-buffer
                    var fluidBuffer = section.getConfigurationSection("fluid-buffer");
                    if (fluidBuffer != null) {
                        try (var ignored2 = StackFormatter.setPosition("Reading fluid-buffer section: " + key)) {

                        List<SingletonFluidBufferBlockData> singletons = new ArrayList<>();
                        for (String k : fluidBuffer.getKeys(false)) {
                            var singleton = Pack.read(fluidBuffer, SingletonFluidBufferBlockData.class, k);
                            singletons.add(singleton);
                        }
                        GlobalVars.putFluidBufferBlockData(id.key(), new FluidBufferBlockData(singletons));

                        } catch (Exception ex) {
                            StackFormatter.handle(ex);
                        }
                    }

                    // multiblock
                    var multiblock = section.getConfigurationSection("multiblock");
                    if (multiblock != null) {
                        try (var ignored2 = StackFormatter.setPosition("Reading multiblock section: " + key)) {

                        Map<Vector3i, PylonSimpleMultiblock.MultiblockComponent> components = new HashMap<>();
                        Map<String, PylonSimpleMultiblock.MultiblockComponent> symbols = new HashMap<>();
                        var blocks = multiblock.getConfigurationSection("blocks");
                        if (blocks != null) {
                            for (String k : blocks.getKeys(false)) {
                                var component = Deserializer.MULTIBLOCK_COMPONENT.deserialize(blocks.getString(k));
                                if (component == null) throw new InvalidMultiblockComponentException(k);
                                symbols.put(k, component);
                            }
                        }

                        var positions = multiblock.getConfigurationSection("positions");
                        if (positions != null) {
                            for (String k : positions.getKeys(false)) {
                                var position = Deserializer.VECTOR3I.deserialize(k);
                                if (position == null) throw new IncompatibleKeyFormatException("unknown vector3i: " + k);
                                var component = symbols.get(positions.get(k));
                                if (component == null) throw new UnknownSymbolException("component not found: " + k);
                                components.put(position, component);
                            }
                        }

                        GlobalVars.putMultiBlockComponents(id.key(), components);

                        } catch (Exception ex) {
                            StackFormatter.handle(ex);
                        }

                        // logistic
                        var logistic = section.getConfigurationSection("logistic");
                        CharArrayList invSlotChars = new CharArrayList();
                        if (logistic != null) {
                            try (var ignored2 = StackFormatter.setPosition("Reading logistic section: " + key)) {

                            List<SingletonLogisticBlockData> singletons = new ArrayList<>();
                            for (String k : logistic.getKeys(false)) {
                                var singleton = Pack.read(logistic, SingletonLogisticBlockData.class, k);
                                singletons.add(singleton);
                            }
                            GlobalVars.putLogisticBlockData(id.key(), new LogisticBlockData(id.key(), singletons));

                            for (var singleton : singletons) {
                                invSlotChars.add(singleton.invSlotChar());
                            }

                            } catch (Exception ex) {
                                StackFormatter.handle(ex);
                            }
                        }

                        // gui
                        var gui = GuiReader.read(section, namespace, scriptdesc);
                        if (gui != GuiReader.Result.EMPTY)
                            GlobalVars.putGui(id.key(), new GuiData(id.key(), gui.structure(), gui.provider(), Gui.normal(), null, invSlotChars));
                    }
                } catch (Exception e) {
                    StackFormatter.handle(e);
                }}
            } catch (Exception e) {
                StackFormatter.handle(e);
            }}

            return this;
        });
    }
    // @formatter:off
}

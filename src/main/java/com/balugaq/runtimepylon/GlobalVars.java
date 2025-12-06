package com.balugaq.runtimepylon;

import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.object.KeyedMap;
import com.balugaq.runtimepylon.script.ScriptExecutor;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Vector3i;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.gui.Gui;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author balugaq
 */
@NullMarked
public class GlobalVars {
    private static final @Getter KeyedMap<ScriptExecutor> scripts = new KeyedMap<>();
    private static final @Getter KeyedMap<Gui> guis = new KeyedMap<>();
    private static final @Getter KeyedMap<SimpleStaticGuidePage> customPages = new KeyedMap<>();
    private static final @Getter KeyedMap<FluidBlockData> fluidBlockDatas = new KeyedMap<>();
    private static final @Getter KeyedMap<FluidBufferBlockData> fluidBufferBlockDatas = new KeyedMap<>();
    private static final @Getter KeyedMap<Map<Vector3i, PylonSimpleMultiblock.MultiblockComponent>> multiBlockComponents = new KeyedMap<>();

    static {
        guis.defaultReturnValue(Gui.normal().build());
        fluidBlockDatas.defaultReturnValue(FluidBlockData.EMPTY);
        fluidBufferBlockDatas.defaultReturnValue(FluidBufferBlockData.EMPTY);
        multiBlockComponents.defaultReturnValue(Map.of());
    }

    public static void destroy() {
        guis.clear();
    }

    public static Gui putGui(NamespacedKey key, Gui gui) {
        guis.put(key, gui);
        return gui;
    }

    public static Gui getGui(NamespacedKey key) {
        return guis.get(key);
    }

    public static GlobalVars.Result<Gui> getGuiO(NamespacedKey key) {
        return GlobalVars.Result.of(guis.get(key));
    }
    public static void putScript(NamespacedKey key, ScriptExecutor script) {
        scripts.put(key, script);
    }

    public static ScriptExecutor getScript(NamespacedKey key) {
        return scripts.get(key);
    }

    public static GlobalVars.Result<ScriptExecutor> getScriptO(NamespacedKey key) {
        return GlobalVars.Result.of(scripts.get(key));
    }

    public static void putCustomPage(NamespacedKey key, SimpleStaticGuidePage page) {
        customPages.put(key, page);
    }

    public static SimpleStaticGuidePage getCustomPage(NamespacedKey key) {
        return customPages.get(key);
    }

    public static GlobalVars.Result<SimpleStaticGuidePage> getCustomPageO(NamespacedKey key) {
        return GlobalVars.Result.of(customPages.get(key));
    }

    public static void putFluidBlockData(NamespacedKey key, FluidBlockData data) {
        fluidBlockDatas.put(key, data);
    }

    public static FluidBlockData getFluidBlockData(NamespacedKey key) {
        return fluidBlockDatas.get(key);
    }

    public static GlobalVars.Result<FluidBlockData> getFluidBlockDataO(NamespacedKey key) {
        return GlobalVars.Result.of(fluidBlockDatas.get(key));
    }

    public static void putFluidBufferBlockData(NamespacedKey key, FluidBufferBlockData data) {
        fluidBufferBlockDatas.put(key, data);
    }

    public static FluidBufferBlockData getFluidBufferBlockData(NamespacedKey key) {
        return fluidBufferBlockDatas.get(key);
    }

    public static GlobalVars.Result<FluidBufferBlockData> getFluidBufferBlockDataO(NamespacedKey key) {
        return GlobalVars.Result.of(fluidBufferBlockDatas.get(key));
    }

    public static void putMultiBlockComponents(NamespacedKey key, Map<Vector3i, PylonSimpleMultiblock.MultiblockComponent> components) {
        multiBlockComponents.put(key, components);
    }

    public static Map<Vector3i, PylonSimpleMultiblock.MultiblockComponent> getMultiBlockComponents(NamespacedKey key) {
        return multiBlockComponents.get(key);
    }

    /**
     * @author balugaq
     */
    @NullMarked
    public static class Result<T> extends ResultL2<T> {
        @Unmodifiable
        public static final Result<?> EMPTY = Result.of(null);

        public Result(@Nullable T result) {
            super(result);
        }

        public static <T> Result<T> of(@Nullable T result) {
            return new Result<>(result);
        }

        public boolean isPresent() {
            return result != null;
        }

        public <K> boolean isPresent(Class<K> clazz) {
            return clazz.isInstance(result);
        }

        @UnknownNullability
        public T get() {
            return result;
        }

        @CanIgnoreReturnValue
        public ResultL2<T> ifPresent(Consumer<T> consumer) {
            if (result != null) {
                consumer.accept(result);
            }
            return this;
        }

        public <K> ResultL2<T> ifPresent(Class<K> clazz, Consumer<K> consumer) {
            if (result != null && clazz.isInstance(result)) {
                consumer.accept(clazz.cast(result));
            }
            return this;
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    public static class ResultL2<T> {
        @Nullable
        public final T result;
        public ResultL2(@Nullable T result) {
            this.result = result;
        }

        public void elseThen(Runnable runnable) {
            if (result == null) {
                runnable.run();
            }
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    public record FluidBlockData(@Unmodifiable List<SingletonFluidBlockData> data) implements Iterable<SingletonFluidBlockData> {
        @Unmodifiable
        public static final FluidBlockData EMPTY = new FluidBlockData(List.of());

        @Override
        public Iterator<SingletonFluidBlockData> iterator() {
            return data.iterator();
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    public record SingletonFluidBlockData(FluidPointType fluidPointType, BlockFace face, boolean allowVerticalFaces) implements Deserializer<SingletonFluidBlockData> {
        public SingletonFluidBlockData() {
            this(FluidPointType.INTERSECTION, BlockFace.NORTH, true);
        }

        @Override
        public List<ConfigReader<?, SingletonFluidBlockData>> readers() {
            return List.of(ConfigReader.of(ConfigurationSection.class, section -> {
                FluidPointType type = Optional.ofNullable(Pack.readEnumOrNull(section, FluidPointType.class, "type"))
                        .orElse(FluidPointType.INTERSECTION);
                BlockFace face = Optional.ofNullable(Pack.readEnumOrNull(section, BlockFace.class, "face"))
                        .orElse(BlockFace.NORTH);
                return new SingletonFluidBlockData(type, face, section.getBoolean("allowVerticalFaces", true));
            }));
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    public record FluidBufferBlockData(@Unmodifiable List<SingletonFluidBufferBlockData> data) implements Iterable<SingletonFluidBufferBlockData> {
        @Unmodifiable
        public static final FluidBufferBlockData EMPTY = new FluidBufferBlockData(List.of());

        public FluidBufferBlockData(List<SingletonFluidBufferBlockData> data) {
            this.data = data;
        }

        @Override
        public Iterator<SingletonFluidBufferBlockData> iterator() {
            return data.iterator();
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    public record SingletonFluidBufferBlockData(PylonFluid fluid, double capacity, boolean input, boolean output) implements Deserializer<SingletonFluidBufferBlockData> {
        public SingletonFluidBufferBlockData() {
            this(null, 0, true, true);
        }

        @Override
        public List<ConfigReader<?, SingletonFluidBufferBlockData>> readers() {
            return List.of(ConfigReader.of(ConfigurationSection.class, section -> {
                PylonFluid fluid = Deserializer.PYLON_FLUID.deserialize(section.get("fluid"));
                double capacity = section.getDouble("capacity", 0);
                boolean input = section.getBoolean("input", true);
                boolean output = section.getBoolean("output", true);
                return new SingletonFluidBufferBlockData(fluid, capacity, input, output);
            }));
        }
    }
}

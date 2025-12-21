package com.balugaq.runtimepylon;

import com.balugaq.runtimepylon.config.FluidBlockData;
import com.balugaq.runtimepylon.config.FluidBufferBlockData;
import com.balugaq.runtimepylon.config.LogisticBlockData;
import com.balugaq.runtimepylon.data.KeyedMap;
import com.balugaq.runtimepylon.script.ScriptExecutor;
import com.balugaq.runtimepylon.script.callbacks.APICallbacks;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Vector3i;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author balugaq
 */
@NullMarked
public class GlobalVars {
    public static final @Getter Gui PLACEHOLDER_GUI = Gui.normal().setStructure("B B B B B B B B B").addIngredient('B', ItemProvider.EMPTY).build();
    public static final @Getter Key PLACEHOLDER_KEY = com.balugaq.runtimepylon.util.Key.create("placeholder").key();
    private static final @Getter KeyedMap<ScriptExecutor> scripts = new KeyedMap<>();
    private static final @Getter KeyedMap<Gui> guis = new KeyedMap<>();
    private static final @Getter KeyedMap<SimpleStaticGuidePage> customPages = new KeyedMap<>();
    private static final @Getter KeyedMap<FluidBlockData> fluidBlockDatas = new KeyedMap<>();
    private static final @Getter KeyedMap<FluidBufferBlockData> fluidBufferBlockDatas = new KeyedMap<>();
    private static final @Getter KeyedMap<Map<Vector3i, PylonSimpleMultiblock.MultiblockComponent>> multiBlockComponents = new KeyedMap<>();
    private static final @Getter KeyedMap<LogisticBlockData> logisticBlockDatas = new KeyedMap<>();
    private static final @Getter KeyedMap<RecipeType<?>> loadRecipeTypes = new KeyedMap<>();
    private static final @Getter KeyedMap<Key> equipmentTypes = new KeyedMap<>();
    private static final @Getter V8Runtime scriptRuntime;
    static {
        try {
            scriptRuntime = V8Host.getV8Instance().createV8Runtime();
            scriptRuntime.getGlobalObject().bind(new APICallbacks());
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    public static final LegacyComponentSerializer COMPONENT_SERIALIZER = LegacyComponentSerializer.legacyAmpersand()
            .toBuilder()
            .hexColors()
            .build();

    static {
        guis.defaultReturnValue(PLACEHOLDER_GUI);
        fluidBlockDatas.defaultReturnValue(FluidBlockData.EMPTY);
        fluidBufferBlockDatas.defaultReturnValue(FluidBufferBlockData.EMPTY);
        multiBlockComponents.defaultReturnValue(Map.of());
        logisticBlockDatas.defaultReturnValue(LogisticBlockData.EMPTY);
        equipmentTypes.defaultReturnValue(PLACEHOLDER_KEY);
    }

    public static void destroy() {
        guis.clear();
    }

    @CanIgnoreReturnValue
    public static Gui putGui(NamespacedKey key, Gui gui) {
        guis.put(key, gui);
        return gui;
    }

    @Nullable
    public static Gui getGui(NamespacedKey key) {
        return guis.get(key);
    }

    public static Result<Gui> getGuiO(NamespacedKey key) {
        return Result.of(guis.get(key));
    }

    @CanIgnoreReturnValue
    public static ScriptExecutor putScript(NamespacedKey key, ScriptExecutor script) {
        scripts.put(key, script);
        return script;
    }

    public static ScriptExecutor getScript(NamespacedKey key) {
        return scripts.get(key);
    }

    public static Result<ScriptExecutor> getScriptO(NamespacedKey key) {
        return Result.of(scripts.get(key));
    }

    @CanIgnoreReturnValue
    public static SimpleStaticGuidePage putCustomPage(NamespacedKey key, SimpleStaticGuidePage page) {
        customPages.put(key, page);
        return page;
    }

    public static SimpleStaticGuidePage getCustomPage(NamespacedKey key) {
        return customPages.get(key);
    }

    public static Result<SimpleStaticGuidePage> getCustomPageO(NamespacedKey key) {
        return Result.of(customPages.get(key));
    }

    @CanIgnoreReturnValue
    public static FluidBlockData putFluidBlockData(NamespacedKey key, FluidBlockData data) {
        fluidBlockDatas.put(key, data);
        return data;
    }

    public static FluidBlockData getFluidBlockData(NamespacedKey key) {
        return fluidBlockDatas.get(key);
    }

    public static Result<FluidBlockData> getFluidBlockDataO(NamespacedKey key) {
        return Result.of(fluidBlockDatas.get(key));
    }

    @CanIgnoreReturnValue
    public static FluidBufferBlockData putFluidBufferBlockData(NamespacedKey key, FluidBufferBlockData data) {
        fluidBufferBlockDatas.put(key, data);
        return data;
    }

    public static FluidBufferBlockData getFluidBufferBlockData(NamespacedKey key) {
        return fluidBufferBlockDatas.get(key);
    }

    public static Result<FluidBufferBlockData> getFluidBufferBlockDataO(NamespacedKey key) {
        return Result.of(fluidBufferBlockDatas.get(key));
    }

    @CanIgnoreReturnValue
    public static Map<Vector3i, PylonSimpleMultiblock.MultiblockComponent> putMultiBlockComponents(NamespacedKey key, Map<Vector3i, PylonSimpleMultiblock.MultiblockComponent> components) {
        multiBlockComponents.put(key, components);
        return components;
    }

    public static Map<Vector3i, PylonSimpleMultiblock.MultiblockComponent> getMultiBlockComponents(NamespacedKey key) {
        return multiBlockComponents.get(key);
    }

    @CanIgnoreReturnValue
    public static LogisticBlockData putLogisticBlockData(NamespacedKey key, LogisticBlockData data) {
        logisticBlockDatas.put(key, data);
        return data;
    }

    public static LogisticBlockData getLogisticBlockData(NamespacedKey key) {
        return logisticBlockDatas.get(key);
    }

    public static Result<LogisticBlockData> getLogisticBlockDataO(NamespacedKey key) {
        return Result.of(logisticBlockDatas.get(key));
    }

    @CanIgnoreReturnValue
    public static RecipeType<?> putLoadRecipeType(NamespacedKey key, RecipeType<?> recipeType) {
        loadRecipeTypes.put(key, recipeType);
        return recipeType;
    }

    @Nullable
    public static RecipeType<?> getLoadRecipeType(NamespacedKey key) {
        return loadRecipeTypes.get(key);
    }

    public static Result<RecipeType<?>> getLoadRecipeTypeO(NamespacedKey key) {
        return Result.of(loadRecipeTypes.get(key));
    }

    @CanIgnoreReturnValue
    public static Key putEquipmentType(NamespacedKey key, Key equipmentType) {
        equipmentTypes.put(key, equipmentType);
        return equipmentType;
    }

    public static Key getEquipmentType(NamespacedKey key) {
        return equipmentTypes.get(key);
    }

    public static Result<Key> getEquipmentTypeO(NamespacedKey key) {
        return Result.of(equipmentTypes.get(key));
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

        @CanIgnoreReturnValue
        public ResultL2<T> ifPresent(Supplier<T> callable) {
            return new ResultL2<>(callable.get());
        }

        @CanIgnoreReturnValue
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
    public static @Getter class ResultL2<T> {
        @Nullable
        protected final T result;

        protected ResultL2(@Nullable T result) {
            this.result = result;
        }

        public void elseThen(Runnable runnable) {
            if (result == null) {
                runnable.run();
            }
        }

        @UnknownNullability
        public T orElse(@Nullable T other) {
            return result != null ? result : other;
        }

        public boolean isEmpty() {
            return result == null;
        }

        public boolean isPresent() {
            return result != null;
        }
    }
}

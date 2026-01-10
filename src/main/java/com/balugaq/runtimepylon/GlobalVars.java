package com.balugaq.runtimepylon;

import com.balugaq.runtimepylon.config.FluidBlockData;
import com.balugaq.runtimepylon.config.FluidBufferBlockData;
import com.balugaq.runtimepylon.config.GuiData;
import com.balugaq.runtimepylon.config.LogisticBlockData;
import com.balugaq.runtimepylon.data.KeyedMap;
import com.balugaq.runtimepylon.object.CustomPageButton;
import com.balugaq.runtimepylon.script.ScriptExecutor;
import com.balugaq.runtimepylon.script.callbacks.APICallbacks;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.guide.button.PageButton;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Vector3i;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author balugaq
 */
@NullMarked
public class GlobalVars {
    private static final @Getter KeyedMap<ScriptExecutor> scripts = new KeyedMap<>();
    private static final @Getter KeyedMap<GuiData> guis = new KeyedMap<>();
    private static final @Getter KeyedMap<CustomPageButton> customPages = new KeyedMap<>();
    private static final @Getter KeyedMap<FluidBlockData> fluidBlockDatas = new KeyedMap<>();
    private static final @Getter KeyedMap<FluidBufferBlockData> fluidBufferBlockDatas = new KeyedMap<>();
    private static final @Getter KeyedMap<Map<Vector3i, PylonSimpleMultiblock.MultiblockComponent>> multiBlockComponents = new KeyedMap<>();
    private static final @Getter KeyedMap<LogisticBlockData> logisticBlockDatas = new KeyedMap<>();
    private static final @Getter KeyedMap<Key> equipmentTypes = new KeyedMap<>();
    private static final @Getter V8Runtime scriptRuntime;
    private static final File PACKS_FOLDER = new File(RuntimePylon.getInstance().getDataFolder(), "packs");
    private static final File ERROR_REPORTS_FOLDER = new File(RuntimePylon.getInstance().getDataFolder(), "error-reports");
    private static final File PACK_UPDATE_DOWNLOAD_FOLDER = new File(RuntimePylon.getInstance().getDataFolder(), "pack-update-downloads");

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
        multiBlockComponents.defaultReturnValue(Map.of());
    }

    public static void destroy() {
        guis.clear();
    }

    @CanIgnoreReturnValue
    public static GuiData putGuiData(NamespacedKey key, GuiData data) {
        guis.put(key, data);
        return data;
    }

    @Nullable
    public static GuiData getGuiData(NamespacedKey key) {
        return guis.get(key);
    }

    public static Result<GuiData> getGuiDataO(NamespacedKey key) {
        return Result.of(getGuiData(key));
    }

    @CanIgnoreReturnValue
    public static ScriptExecutor putScript(NamespacedKey key, ScriptExecutor script) {
        scripts.put(key, script);
        return script;
    }

    @Nullable
    public static ScriptExecutor getScript(NamespacedKey key) {
        return scripts.get(key);
    }

    public static Result<ScriptExecutor> getScriptO(NamespacedKey key) {
        return Result.of(getScript(key));
    }

    @CanIgnoreReturnValue
    public static CustomPageButton putCustomPage(NamespacedKey key, CustomPageButton page) {
        customPages.put(key, page);
        return page;
    }

    @Nullable
    public static CustomPageButton getCustomPage(NamespacedKey key) {
        return customPages.get(key);
    }

    public static Result<PageButton> getCustomPageO(NamespacedKey key) {
        return Result.of(getCustomPage(key));
    }

    @CanIgnoreReturnValue
    public static FluidBlockData putFluidBlockData(NamespacedKey key, FluidBlockData data) {
        fluidBlockDatas.put(key, data);
        return data;
    }

    @Nullable
    public static FluidBlockData getFluidBlockData(NamespacedKey key) {
        return fluidBlockDatas.get(key);
    }

    public static Result<FluidBlockData> getFluidBlockDataO(NamespacedKey key) {
        return Result.of(getFluidBlockData(key));
    }

    @CanIgnoreReturnValue
    public static FluidBufferBlockData putFluidBufferBlockData(NamespacedKey key, FluidBufferBlockData data) {
        fluidBufferBlockDatas.put(key, data);
        return data;
    }

    @Nullable
    public static FluidBufferBlockData getFluidBufferBlockData(NamespacedKey key) {
        return fluidBufferBlockDatas.get(key);
    }

    public static Result<FluidBufferBlockData> getFluidBufferBlockDataO(NamespacedKey key) {
        return Result.of(getFluidBufferBlockData(key));
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

    @Nullable
    public static LogisticBlockData getLogisticBlockData(NamespacedKey key) {
        return logisticBlockDatas.get(key);
    }

    public static Result<LogisticBlockData> getLogisticBlockDataO(NamespacedKey key) {
        return Result.of(getLogisticBlockData(key));
    }

    @CanIgnoreReturnValue
    public static Key putEquipmentType(NamespacedKey key, Key equipmentType) {
        equipmentTypes.put(key, equipmentType);
        return equipmentType;
    }

    @Nullable
    public static Key getEquipmentType(NamespacedKey key) {
        return equipmentTypes.get(key);
    }

    public static Result<Key> getEquipmentTypeO(NamespacedKey key) {
        return Result.of(getEquipmentType(key));
    }

    public static File getErrorReportsFolder() {
        return ERROR_REPORTS_FOLDER;
    }

    public static File getPackUpdateDownloadFolder() {
        return PACK_UPDATE_DOWNLOAD_FOLDER;
    }

    public static File getPacksFolder() {
        return PACKS_FOLDER;
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

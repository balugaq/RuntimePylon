package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.pack.PackID;
import com.balugaq.runtimepylon.exceptions.DeserializationException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.exceptions.UnknownEnumException;
import com.balugaq.runtimepylon.exceptions.UnknownItemException;
import com.balugaq.runtimepylon.exceptions.UnknownMultiblockComponentException;
import com.balugaq.runtimepylon.exceptions.UnknownSaveditemException;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.display.BlockDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3i;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * This interface is used to deserialize a config structure, instead of file structure For the example:
 * <p>
 * <code>
 * <pre>
 * &#064;NoArgsConstructor(force = true)
 * class Foo implements Deserializer<Foo> {}
 * </pre>
 * </code>
 * <p>
 * The class `Foo` must be annotated with {@code @lombok.NoArgsConstructor(force = true)} to make
 * {@link #newDeserializer(Class)} work.
 *
 * @param <T>
 *         the type of the object.
 *
 * @author balugaq
 * @see PackID
 */
@FunctionalInterface
@NullMarked
public interface Deserializer<T> {
    ItemStackDeserializer ITEMSTACK = new ItemStackDeserializer();
    PylonFluidDeserializer PYLON_FLUID = new PylonFluidDeserializer();
    MultiblockComponentDeserializer MULTIBLOCK_COMPONENT = new MultiblockComponentDeserializer();
    Vector3iDeserializer VECTOR3I = new Vector3iDeserializer();

    static <E extends Enum<E>> EnumDeserializer<E> enumDeserializer(Class<E> clazz) {
        return EnumDeserializer.of(clazz);
    }

    /**
     * Create an instance of the object. All the data in this object are invalid. It just for call
     * {@link #deserialize(Object)}.
     *
     * @return an instance of the object.
     *
     * @author balugaq
     * @see #deserialize(Object)
     */
    @Internal
    static <T extends Deserializer<T>> T newDeserializer(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new DeserializationException(clazz, e);
        }
    }

    /**
     * Unserializes an object.
     *
     * @param o
     *         the object to deserialize, it may be {@link ConfigurationSection}, {@link ArrayList}, or primitive type.
     *
     * @return an instance of the object.
     *
     * @author balugaq
     * @see Deserializer#newDeserializer(Class)
     */
    @UnknownNullability
    default T deserialize(@Nullable Object o) throws DeserializationException {
        if (o == null) throw new MissingArgumentException(this.getClass());
        for (ConfigReader<?, T> reader : readers()) {
            if (reader.type().isInstance(o)) {
                try {
                    @SuppressWarnings("unchecked") T v = (T) ReflectionUtil.invokeMethod(reader, "read", o);
                    if (v != null) return v;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new DeserializationException(getClass(), e.getCause());
                }
            }
        }

        throw new DeserializationException(this.getClass());
    }

    List<ConfigReader<?, T>> readers();

    @NullMarked
    class ItemStackDeserializer implements Deserializer<ItemStack> {
        public static final Map<String, String> FIELD_RENAME = Map.of(
                "grass", "short_grass", "short_grass", "grass",
                "scute", "turtle_scute", "turtle_scute", "scute",
                "chain", "iron_chain", "iron_chain", "chain"
        );

        @Override
        public @UnknownNullability ItemStack deserialize(@Nullable Object o) {
            try (var ignore = StackFormatter.setPosition("Reading ItemStack")) {
                if (o == null) throw new MissingArgumentException();
                for (ConfigReader<?, ItemStack> reader : readers()) {
                    if (reader.type().isInstance(o)) {
                        try {
                            return (ItemStack) ReflectionUtil.invokeMethod(reader, "read", o);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw e.getCause();
                        }
                    }
                }
                return null;
            } catch (Throwable e) {
                StackFormatter.handle(e);
                return null;
            }
        }

        @Internal
        @SuppressWarnings("DuplicateCondition")
        private static ItemStack fromString(String s) throws DeserializationException {
            List<String> para = new ArrayList<>();
            if (s.contains("|")) {
                String[] split = s.split("\\|");
                for (String s2 : split) {
                    para.add(s2.trim());
                }
            } else {
                para.add(s.trim());
            }

            for (String s2 : para) {
                if (s2.startsWith("minecraft:") || !s2.contains(":")) {
                    // item: minecraft:diamond
                    // item: diamond
                    //
                    // minecraft item
                    String fixed;
                    if (s2.startsWith("minecraft:")) {
                        fixed = s2.substring(10);
                    } else {
                        fixed = s2;
                    }

                    Material material = Material.getMaterial(fixed.toUpperCase());
                    if (material == null) {
                        // try to rename field
                        String rename = FIELD_RENAME.get(fixed);
                        if (rename != null) {
                            material = Material.getMaterial(rename.toUpperCase());
                        }
                    }

                    if (material != null) {
                        return new ItemStack(material);
                    }
                } else if (s2.startsWith("saveditem")) {
                    // item: saveditem:mypack:foo
                    // also supports saveditem:mypack:blocks/bar
                    if (s2.contains(":")) {
                        // item: saveditem:mypack:foo
                        String[] split = s2.split(":");
                        String pack = split[1];
                        String fileName = split[2];
                        return PackManager.findSaveditem(
                                Deserializer.newDeserializer(PackDesc.class).deserialize(pack),
                                Deserializer.newDeserializer(SaveditemDesc.class).deserialize(fileName)
                        );
                    } else {
                        // item: saveditem:foo
                        // also supports saveditem:blocks/foo
                        // find from all packs
                        for (Pack pack : PackManager.getPacks()) {
                            try {
                                return PackManager.findSaveditem(
                                        Deserializer.newDeserializer(PackDesc.class).deserialize(pack),
                                        Deserializer.newDeserializer(SaveditemDesc.class).deserialize(s2)
                                );
                            } catch (UnknownSaveditemException ignored) {
                            }
                        }
                        throw new UnknownSaveditemException(s2);
                    }
                } else if (s2.contains(":")) {
                    // item: pylonbase:loupe
                    // get item from pylon registry
                    NamespacedKey k = NamespacedKey.fromString(s2);
                    if (k == null) continue;

                    PylonItemSchema schema = PylonRegistry.ITEMS.get(k);
                    if (schema != null) {
                        return schema.getItemStack();
                    }
                }
            }

            throw new UnknownItemException(s);
        }

        @Override
        public List<ConfigReader<?, ItemStack>> readers() {
            return List.of(
                    ConfigReader.of(String.class, ItemStackDeserializer::fromString),
                    ConfigReader.of(
                            ConfigurationSection.class, section -> {
                                // for section, we have these fields for optional
                                // item:
                                //   material: minecraft:diamond // or heads: hash/base64/url
                                //   amount: 1-99
                                //   compounds... (// todo)

                                String s = section.getString("material");
                                if (s == null) throw new MissingArgumentException("material");
                                try {
                                    ItemStack item = fromString(s).clone();
                                    item.setAmount(section.getInt("amount", 1));
                                    return item;
                                } catch (UnknownItemException e) {
                                    try {
                                        return ConfigAdapter.ITEM_STACK.convert(section);
                                    } catch (IllegalArgumentException e2) {
                                        e2.addSuppressed(e);
                                        throw e2;
                                    }
                                }
                            }
                    )
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class EnumDeserializer<E extends Enum<E>> implements Deserializer<E> {
        private final Class<E> clazz;
        private Function<String, String> preHandle = s -> s;

        public EnumDeserializer(Class<E> clazz) {
            this.clazz = clazz;
        }

        public static <E extends Enum<E>> EnumDeserializer<E> of(Class<E> clazz) {
            return new EnumDeserializer<>(clazz);
        }

        public EnumDeserializer<E> forceUpperCase() {
            return updatePreHandle(String::toUpperCase);
        }

        public EnumDeserializer<E> updatePreHandle(Function<String, String> preHandle) {
            this.preHandle = preHandle.andThen(preHandle);
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public @UnknownNullability E deserialize(@Nullable Object o) {
            try (var ignore = StackFormatter.setPosition("Reading " + clazz.getSimpleName())) {
                if (o == null) throw new MissingArgumentException();
                for (ConfigReader<?, E> reader : readers()) {
                    if (reader.type().isInstance(o)) {
                        try {
                            return (E) ReflectionUtil.invokeMethod(reader, "read", o);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw e.getCause();
                        }
                    }
                }
                return null;
            } catch (Throwable e) {
                if (e instanceof IllegalArgumentException e2) {
                    StackFormatter.handle(new UnknownEnumException(clazz, e2.getMessage()));
                } else {
                    StackFormatter.handle(e);
                }
                return null;
            }
        }

        @Override
        public List<ConfigReader<?, E>> readers() {
            return List.of(
                    ConfigReader.of(String.class, s -> Enum.valueOf(clazz, preHandle.apply(s)))
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class PylonFluidDeserializer implements Deserializer<PylonFluid> {
        @Override
        public List<ConfigReader<?, PylonFluid>> readers() {
            return List.of(
                    ConfigReader.of(
                            String.class, s -> {
                                NamespacedKey key = NamespacedKey.fromString(s);
                                if (key == null) return null;

                                return PylonRegistry.FLUIDS.get(key);
                            }
                    )
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class MultiblockComponentDeserializer implements Deserializer<PylonSimpleMultiblock.MultiblockComponent> {
        @Override
        public List<ConfigReader<?, PylonSimpleMultiblock.MultiblockComponent>> readers() {
            return List.of(
                    ConfigReader.of(
                            String.class, s -> {
                                List<PylonSimpleMultiblock.MultiblockComponent> list = new ArrayList<>();
                                if (s.contains("|")) {
                                    for (var s2 : s.split("\\|")) {
                                        list.add(Deserializer.MULTIBLOCK_COMPONENT.deserialize(s2.trim()));
                                    }
                                } else {
                                    if (s.startsWith("minecraft:")) {
                                        if (s.contains("[")) {
                                            var mat = s.substring(10, s.indexOf("["));
                                            Material material = Deserializer.enumDeserializer(Material.class).deserialize(mat);
                                            var data = s.substring(s.indexOf("[") + 1);
                                            list.add(new PylonSimpleMultiblock.VanillaBlockdataMultiblockComponent(material.createBlockData(data)));
                                        } else {
                                            var mat = s.substring(10);
                                            list.add(new PylonSimpleMultiblock.VanillaMultiblockComponent(Deserializer.enumDeserializer(Material.class).deserialize(mat)));
                                        }
                                    } else {
                                        var key = NamespacedKey.fromString(s);
                                        if (key == null) throw new UnknownMultiblockComponentException(s);
                                        if (!PylonRegistry.BLOCKS.contains(key)) {
                                            throw new UnknownMultiblockComponentException(s);
                                        }
                                        list.add(new PylonSimpleMultiblock.PylonMultiblockComponent(key));
                                    }
                                }

                                List<BlockData> blockDataList = list.stream().map(c -> switch (c) {
                                    case PylonSimpleMultiblock.PylonMultiblockComponent c2 ->
                                            List.of(PylonRegistry.BLOCKS.get(c2.key()).getMaterial().createBlockData());
                                    case PylonSimpleMultiblock.VanillaMultiblockComponent c2 ->
                                            c2.component1().stream().map(Material::createBlockData).toList();
                                    case PylonSimpleMultiblock.VanillaBlockdataMultiblockComponent c2 ->
                                            c2.component1();
                                    case MyMultiBlockComponent c2 -> c2.getBlockDataList();
                                    default -> null;
                                }).filter(Objects::nonNull).flatMap(Collection::stream).toList();

                                return new MyMultiBlockComponent(list, blockDataList);
                            }
                    )
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class Vector3iDeserializer implements Deserializer<Vector3i> {
        @Override
        public List<ConfigReader<?, Vector3i>> readers() {
            return List.of(
                    ConfigReader.of(
                            String.class, s -> {
                                String[] split = s.split(";");
                                if (split.length != 3) throw new IllegalArgumentException("Invalid vector3i format");
                                return new Vector3i(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                            }
                    )
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    @Data
    @RequiredArgsConstructor
    class MyMultiBlockComponent implements PylonSimpleMultiblock.MultiblockComponent {
        private final List<? extends PylonSimpleMultiblock.MultiblockComponent> components;
        private final List<BlockData> blockDataList;

        @Override
        public boolean matches(@NotNull final Block block) {
            return components.stream().anyMatch(c -> c.matches(block));
        }

        @Override
        public @NotNull UUID spawnGhostBlock(@NotNull final Block block) {

            var display = new BlockDisplayBuilder()
                    .material(blockDataList.getFirst().getMaterial())
                    .glow(Color.WHITE)
                    .transformation(new TransformBuilder().scale(0.5))
                    .build(block.getLocation().toCenterLocation());
            EntityStorage.add(new PylonSimpleMultiblock.MultiblockGhostBlock(display, String.join(", ", blockDataList.stream().map(Object::toString).toList())));

            if (blockDataList.size() > 1) {
                AtomicInteger i = new AtomicInteger(0);
                RuntimePylon.runTaskTimer(
                        () -> {
                            while (display.isValid()) {
                                display.setBlock(blockDataList.get(i.getAndIncrement()));
                                i.set(i.get() % blockDataList.size());
                            }
                        }, 20, 20
                );
            }

            return display.getUniqueId();
        }
    }
}

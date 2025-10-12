package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.config.pack.PackID;
import com.balugaq.runtimepylon.exceptions.DeserializationException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.exceptions.UnknownItemException;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This interface is used to deserialize a config structure, instead of file structure
 * For the example:
 * <p>
 * <code>
 * <pre>
 * &#064;NoArgsConstructor(force  = true)
 * class Foo implements Deserializer<Foo> {}
 * </pre>
 * </code>
 * <p>
 * The class `Foo` must be annotated with {@code @lombok.NoArgsConstructor(force = true)}
 * to make {@link #newDeserializer(Class)} work.
 *
 * @param <T> the type of the object.
 * @author balugaq
 * @see PackID
 */
@FunctionalInterface
public interface Deserializer<T> {
    ItemStackDeserializer ITEMSTACK = new ItemStackDeserializer();

    @NotNull
    static <E extends Enum<E>> EnumDeserializer<E> enumDeserializer(Class<E> clazz) {
        return EnumDeserializer.of(clazz);
    }

    /**
     * Create an instance of the object.
     * All the data in this object are invalid.
     * It just for call {@link #deserialize(Object)}.
     *
     * @return an instance of the object.
     * @author balugaq
     * @see #deserialize(Object)
     */
    @Internal
    @NotNull
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
     * @param o the object to deserialize, it may be {@link ConfigurationSection}, {@link ArrayList}, or primitive type.
     * @return an instance of the object.
     * @author balugaq
     * @see Deserializer#newDeserializer(Class)
     */
    @UnknownNullability
    default T deserialize(@Nullable Object o) throws DeserializationException {
        if (o == null) throw new MissingArgumentException();
        for (ConfigReader<?, T> reader : readers()) {
            if (reader.type().isInstance(o)) {
                try {
                    @SuppressWarnings("unchecked") T v = (T) ReflectionUtil.invokeMethod(reader, "read", o);
                    if (v != null) return v;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new DeserializationException(getClass(), e);
                }
            }
        }

        throw new DeserializationException(this.getClass());
    }

    @NotNull
    List<ConfigReader<?, T>> readers();

    class ItemStackDeserializer implements Deserializer<ItemStack> {
        public static final Map<String, String> FIELD_RENAME = Map.of(
                "grass", "short_grass", "short_grass", "grass",
                "scute", "turtle_scute", "turtle_scute", "scute",
                "chain", "iron_chain", "iron_chain", "chain"
        );

        @Internal
        @NotNull
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
                } else if (s2.contains(":")) {
                    // item: saveditem:mypack:foo
                    // also supports saveditem:mypack:blocks/bar
                    if (s2.startsWith("saveditem")) {
                        // item: saveditem:mypack:foo
                        String[] split = s2.split(":");
                        String pack = split[1];
                        String fileName = split[2];
                        return PackManager.findSaveditem(
                                Deserializer.newDeserializer(PackDesc.class).deserialize(pack),
                                Deserializer.newDeserializer(SaveditemDesc.class).deserialize(fileName)
                        );
                    }

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

        @NotNull
        @Override
        public List<ConfigReader<?, ItemStack>> readers() {
            return List.of(
                    ConfigReader.of(String.class, ItemStackDeserializer::fromString),
                    ConfigReader.of(ConfigurationSection.class, section -> {
                        // for section, we have these fields for optional
                        // item:
                        //   material: minecraft:diamond // or heads: hash/base64/url
                        //   amount: 1-99
                        //   compounds... (// todo)

                        String s = section.getString("material");
                        if (s == null) throw new MissingArgumentException("material");
                        ItemStack item = fromString(s).clone();
                        item.setAmount(section.getInt("amount", 1));
                        return item;
                    })
            );
        }

        @Nullable
        @Override
        public ItemStack deserialize(@Nullable Object o) {
            try (var ignore = StackWalker.setPosition("Reading ItemStack")) {
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
                StackWalker.handle(e);
                return null;
            }
        }
    }

    class EnumDeserializer<E extends Enum<E>> implements Deserializer<E> {
        private final Class<E> clazz;

        public EnumDeserializer(@NotNull Class<E> clazz) {
            this.clazz = clazz;
        }

        public static <E extends Enum<E>> EnumDeserializer<E> of(@NotNull Class<E> clazz) {
            return new EnumDeserializer<>(clazz);
        }

        @Override
        public @NotNull List<ConfigReader<?, E>> readers() {
            return List.of(
                    ConfigReader.of(String.class, s -> Enum.valueOf(clazz, s))
            );
        }

        @Nullable
        @Override
        public E deserialize(@Nullable Object o) {
            try (var ignore = StackWalker.setPosition("Reading " + clazz.getSimpleName())) {
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
                StackWalker.handle(e);
                return null;
            }
        }
    }
}

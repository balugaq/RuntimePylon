package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.exceptions.DeserializationException;
import com.balugaq.runtimepylon.util.Debug;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.papermc.paper.datacomponent.DataComponentHolder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This interface is used to deserialize a config structure, instead of file structure
 * @param <T> the type of the object.
 *
 * @author balugaq
 * @see Pack
 */
@FunctionalInterface
@NullMarked
public interface Deserializable<T> {
    Deserializable<ItemStack> ITEMSTACK = new ItemStackDeserializer();

    static <E extends Enum<E>> Deserializable<E> enumDeserializer(Class<E> clazz) {
        return () -> List.of(
                ConfigReader.of(String.class, s -> Enum.valueOf(clazz, s.toUpperCase()))
        );
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
    static <T extends Deserializable<T>> T newDeserializer(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new DeserializationException(clazz, e);
        }
    }

    /**
     * Unserializes an object.
     * @param o the object to deserialize, it may be {@link ConfigurationSection}, {@link ArrayList}, or primitive type.
     * @return an instance of the object.
     * @author balugaq
     * @see Deserializable#newDeserializer(Class)
     */
    default T deserialize(@Nullable Object o) throws DeserializationException {
        if (o == null) throw new MissingArgumentException();
        for (ConfigReader<?, T> reader : readers()) {
            if (reader.type().isInstance(o)) {
                @SuppressWarnings("unchecked") T v = (T) ReflectionUtil.invokeMethod(reader, "read", o);
                if (v != null) return v;
            }
        }

        throw new DeserializationException(this.getClass());
    }

    default void analyzeWithStackTrace(String message) {
        // todo
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        Debug.severe(message);
    }

    List<ConfigReader<?, T>> readers();

    class ItemStackDeserializer implements Deserializable<ItemStack> {
        public static final Map<String, String> FIELD_RENAME = Map.of(
                "grass", "short_grass", "short_grass", "grass",
                "scute", "turtle_scute", "turtle_scute", "scute",
                "chain", "iron_chain", "iron_chain", "chain"
        );

        @Override
        public List<ConfigReader<?, ItemStack>> readers() {
            return List.of(
                    ConfigReader.of(String.class, ItemStackDeserializer::fromString),
                    ConfigReader.of(ConfigurationSection.class, section -> {
                        // for section, we have these fields for optional
                        // item:
                        //   material: minecraft:diamond // or heads: hash/base64/url
                        //   amount: 1-99
                        //   compounds... (stashed)

                        String s = section.getString("material");
                        if (s == null) throw new MissingArgumentException("material");
                        ItemStack item = fromString(s).clone();
                        item.setAmount(section.getInt("amount", 1));
                        return item;
                    })
            );
        }

        private static ItemStack fromString(String s) {
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

                    Material material = Material.getMaterial(fixed);
                    if (material == null) {
                        // try rename field
                        String rename = FIELD_RENAME.get(fixed);
                        if (rename != null) {
                            material = Material.getMaterial(rename);
                        }
                    }

                    if (material != null) {
                        return new ItemStack(material);
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
                } else {
                    /*
                    // heads:
                    // 1. base64
                    if (isBase64Like(s2)) {
                        // base64 head
                        ItemStack head = PylonHead.createByBase64(s2);
                        if (head != null) return head;
                    }

                    // 2. url
                    else if (isURLLike(s2)) {
                        ItemStack head = PylonHead.createByURL(s2);
                        if (head != null) return head;
                    }

                    // 3. hashcode
                    else if (isHashcodeLike(s2)) {
                        ItemStack head = PylonHead.createByHashcode(s2);
                        if (head != null) return head;
                    }

                     */

                }
            }

            throw new DeserializationException("Unknown item: " + s);
        }

        private static boolean isHashcodeLike(String value) {
            return value.matches("^[a-fA-F0-9]{32,}$");
        }

        private static boolean isBase64Like(String value) {
            return value.length() > 32 && value.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");
        }

        private static boolean isURLLike(String value) {
            return value.matches("^https?://(?:[-\\w]+\\.)?[-\\w]+(?:\\.[a-zA-Z]{2,5}|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(?::\\d{1,5})?(/[-\\w./]*)*(\\?[-\\w.&=]*)?(#[-\\w]*)?$");
        }
    }
}

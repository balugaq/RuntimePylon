package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.util.Debug;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public interface Unserializable<T> {
    /**
     * Create an instance of the object.
     * All the data in this object are invalid.
     * It just for call {@link #unserialize(Object)}.
     *
     * @return an instance of the object.
     * @author balugaq
     * @see #unserialize(Object)
     */
    @Internal
    static <T extends Unserializable<T>> T newSerializer(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new UnserializableException(clazz, e);
        }
    }

    /**
     * Unserializes an object.
     * @param o the object to unserialize, it may be {@link ConfigurationSection}, {@link ArrayList}, or primitive type.
     * @return an instance of the object.
     * @author balugaq
     * @see Unserializable#newSerializer(Class)
     */
    @Nullable
    default T unserialize(Object o) throws UnserializableException {
        for (Reader<?, T> reader : readers()) {
            if (reader.type().isInstance(o)) {
                @SuppressWarnings("unchecked") T v = (T) ReflectionUtil.invokeMethod(reader, "read", o);
                if (v != null) return v;
            }
        }

        return null;
    }

    default void analyzeWithStackTrace(String message) {
        // todo
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        Debug.severe(message);
    }

    List<Reader<?, T>> readers();
}

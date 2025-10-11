package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.DeserializationException;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface GenericDeserializer<T extends GenericDeserializer<T, K>, K extends Deserializer<K>> extends Deserializer<T> {
    T setGenericType(Class<K> clazz);

    Class<K> getGenericType();
    /**
     * Create an instance of the object.
     * All the data in this object are invalid.
     * It just for call {@link #deserialize(Object)}.
     *
     * @return an instance of the object.
     * @author balugaq
     * @see #deserialize(Object)
     */
    @ApiStatus.Internal
    static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T newDeserializer(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new DeserializationException(clazz, e);
        }
    }
}

package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.DeserializationException;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface GenericDeserializer<T extends GenericDeserializer<T, K>, K extends Deserializer<K>> extends Deserializer<T> {
    @ApiStatus.Internal
    static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T newDeserializer(Class<T> clazz) {
        return newDeserializer(clazz, t -> t);
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
    @ApiStatus.Internal
    static <T extends GenericDeserializer<T, K>, K extends Deserializer<K>> T newDeserializer(Class<T> clazz, Pack.Advancer<K> advancer) {
        try {
            return clazz.getDeclaredConstructor().newInstance().setAdvancer(advancer);
        } catch (Exception e) {
            throw new DeserializationException(clazz, e);
        }
    }

    Class<K> getGenericType();

    T setGenericType(Class<K> clazz);

    T setAdvancer(Pack.Advancer<K> advancer);
}

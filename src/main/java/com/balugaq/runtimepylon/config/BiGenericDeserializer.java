package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.DeserializationException;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public interface BiGenericDeserializer<T extends BiGenericDeserializer<T, K, M>, K extends Deserializer<K>, M extends Deserializer<M>> extends Deserializer<T> {
    @ApiStatus.Internal
    static <T extends BiGenericDeserializer<T, K, M>, K extends Deserializer<K>, M extends Deserializer<M>> T newDeserializer(Class<T> clazz) {
        return newDeserializer(clazz, k -> k, m -> m);
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
    @ApiStatus.Internal
    static <T extends BiGenericDeserializer<T, K, M>, K extends Deserializer<K>, M extends Deserializer<M>> T newDeserializer(Class<T> clazz, Pack.Advancer<K> advancer, Pack.Advancer<M> advancer2) {
        try {
            return clazz.getDeclaredConstructor().newInstance().setAdvancer(advancer).setAdvancer2(advancer2);
        } catch (Exception e) {
            throw new DeserializationException(clazz, e);
        }
    }

    T setAdvancer(Pack.Advancer<K> advancer);

    Class<K> getGenericType();

    T setGenericType(Class<K> clazz);

    T setAdvancer2(Pack.Advancer<M> advancer);

    Class<M> getGenericType2();

    T setGenericType2(Class<M> clazz);
}

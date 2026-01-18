package com.balugaq.pc.config;

import com.balugaq.pc.exceptions.DeserializationException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public interface BiGenericDeserializer<T extends BiGenericDeserializer<T, K, M>, K, M> extends Deserializer<T> {
    @ApiStatus.Internal
    static <T extends BiGenericDeserializer<T, K, M>, K, M> T newDeserializer(Class<T> clazz) {
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
    static <T extends BiGenericDeserializer<T, K, M>, K, M> T newDeserializer(Class<T> clazz, Advancer<Deserializer<K>> advancer, Advancer<Deserializer<M>> advancer2) {
        try {
            return clazz.getDeclaredConstructor().newInstance().setAdvancer(advancer).setAdvancer2(advancer2);
        } catch (Exception e) {
            throw new DeserializationException(clazz, e);
        }
    }

    T setAdvancer(Advancer<Deserializer<K>> advancer);

    @Nullable
    Class<K> getGenericType();

    T setGenericType(Class<K> clazz);

    @MustBeInvokedByOverriders
    @Nullable
    default Deserializer<K> getDeserializer() {
        if (getGenericType().isAssignableFrom(Deserializer.class)) {
            return Deserializer.newDeserializer((Class<? extends Deserializer>) getGenericType());
        }

        return null;
    }

    T setDeserializer(Deserializer<K> deserializer);

    T setAdvancer2(Advancer<Deserializer<M>> advancer);

    @Nullable
    Class<M> getGenericType2();

    T setGenericType2(Class<M> clazz);

    @MustBeInvokedByOverriders
    @Nullable
    default Deserializer<M> getDeserializer2() {
        if (getGenericType().isAssignableFrom(Deserializer.class)) {
            return Deserializer.newDeserializer((Class<? extends Deserializer>) getGenericType());
        }

        return null;
    }

    T setDeserializer2(Deserializer<M> deserializer);
}

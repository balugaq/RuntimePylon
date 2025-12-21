package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.DeserializationException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public interface GenericDeserializer<T extends GenericDeserializer<T, K>, K> extends Deserializer<T> {
    @ApiStatus.Internal
    static <T extends GenericDeserializer<T, K>, K> T newDeserializer(Class<T> clazz) {
        return newDeserializer(clazz, t -> t);
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
    static <T extends GenericDeserializer<T, K>, K> T newDeserializer(Class<T> clazz, Advancer<Deserializer<K>> advancer) {
        try {
            return clazz.getDeclaredConstructor().newInstance().setAdvancer(advancer);
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
        if (Deserializer.class.isAssignableFrom(getGenericType())) {
            return Deserializer.newDeserializer((Class<? extends Deserializer>) getGenericType());
        }

        return null;
    }

    T setDeserializer(Deserializer<K> deserializer);
}

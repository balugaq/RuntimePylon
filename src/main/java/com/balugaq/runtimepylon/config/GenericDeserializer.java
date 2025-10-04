package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.DeserializationException;
import org.jetbrains.annotations.ApiStatus;

public interface GenericDeserializer<T> extends Deserializer<T>, GenericObject<GenericDeserializer<T>, T> {
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
    static <T extends Deserializer<T> & GenericObject<GenericDeserializer<T>, T>> T newDeserializer(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new DeserializationException(clazz, e);
        }
    }
}

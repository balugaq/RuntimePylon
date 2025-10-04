package com.balugaq.runtimepylon.config;

import org.jspecify.annotations.NullMarked;

/**
 * @param <T> The generic type
 * @author balugaq
 */
@NullMarked
public interface GenericObject<K, T> {
    K setGenericType(Class<T> clazz);

    Class<T> getGenericType();
}

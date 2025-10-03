package com.balugaq.runtimepylon.config;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface GenericObject<T> {
    void setGenericType(Class<T> clazz);
    Class<T> getGenericType();
}

package com.balugaq.runtimepylon.config;

import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

/**
 * @author balugaq
 */
@NullMarked
@FunctionalInterface
public interface Advancer<T> extends Function<T, T> {
    default T apply(T object) {
        return advance(object);
    }

    T advance(T object);
}

package com.balugaq.runtimepylon.exceptions;

import org.bukkit.Registry;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author balugaq
 */
@NullMarked
public class UnknownKeyedException extends RuntimeException {
    public UnknownKeyedException() {
        super();
    }

    public UnknownKeyedException(Registry<?> e, String id) {
        super("Unknown keyed: " + getKeyedClass(e).getSimpleName() + "." + id);
    }

    private static Class<?> getKeyedClass(Registry<?> registry) {
        Type genericSuperclass = registry.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof final ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            return (Class<?>) actualTypeArguments[0];
        }
        return Object.class;
    }
}

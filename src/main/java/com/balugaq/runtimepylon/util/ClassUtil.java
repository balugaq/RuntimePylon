package com.balugaq.runtimepylon.util;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author balugaq
 */
@NullMarked
public class ClassUtil {
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> Class<T> getGenericSuperclassType(Class<?> clazz, int index) {
        Type genericSuperclass = clazz.getGenericSuperclass();

        if (!(genericSuperclass instanceof final ParameterizedType parameterizedType)) {
            var superClass = clazz.getSuperclass();
            if (superClass == null) {
                return null;
            }
            return getGenericSuperclassType(superClass, index);
        }

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        if (index < 0 || index >= actualTypeArguments.length) {
            throw new IndexOutOfBoundsException("Generic argument index out of range: " + index);
        }
        
        Type actualType = actualTypeArguments[index];
        if (actualType instanceof Class) {
            return (Class<T>) actualType;
        }

        if (actualType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) actualType).getRawType();
            if (rawType instanceof Class) {
                return (Class<T>) rawType;
            }
        }
        
        return null;
    }

    @Nullable
    public static <T> Class<T> getFirstGenericSuperclassType(Class<?> clazz) {
        return getGenericSuperclassType(clazz, 0);
    }
}
package com.balugaq.runtimepylon.config;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public record LogisticBlockData(@Nullable SingletonLogisticBlockData input, @Nullable SingletonLogisticBlockData output) {
        @Unmodifiable
        public static final LogisticBlockData EMPTY = new LogisticBlockData(null, null);
}

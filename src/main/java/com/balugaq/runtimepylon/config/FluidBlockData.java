package com.balugaq.runtimepylon.config;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Iterator;
import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public record FluidBlockData(
        @Unmodifiable List<SingletonFluidBlockData> data) implements Iterable<SingletonFluidBlockData> {
    @Unmodifiable
    public static final FluidBlockData EMPTY = new FluidBlockData(List.of());

    @Override
    public Iterator<SingletonFluidBlockData> iterator() {
        return data.iterator();
    }
}

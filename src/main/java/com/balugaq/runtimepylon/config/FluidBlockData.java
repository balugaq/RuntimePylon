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
    @Override
    public Iterator<SingletonFluidBlockData> iterator() {
        return data.iterator();
    }
}

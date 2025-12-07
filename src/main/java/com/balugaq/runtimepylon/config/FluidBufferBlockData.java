package com.balugaq.runtimepylon.config;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Iterator;
import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public record FluidBufferBlockData(
        @Unmodifiable List<SingletonFluidBufferBlockData> data) implements Iterable<SingletonFluidBufferBlockData> {
    @Unmodifiable
    public static final FluidBufferBlockData EMPTY = new FluidBufferBlockData(List.of());

    public FluidBufferBlockData(List<SingletonFluidBufferBlockData> data) {
        this.data = data;
    }

    @Override
    public Iterator<SingletonFluidBufferBlockData> iterator() {
        return data.iterator();
    }
}

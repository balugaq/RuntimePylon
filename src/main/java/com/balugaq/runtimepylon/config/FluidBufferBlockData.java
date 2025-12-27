package com.balugaq.runtimepylon.config;

import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author balugaq
 */
@NullMarked
public record FluidBufferBlockData(
        @Unmodifiable List<SingletonFluidBufferBlockData> data) implements Iterable<SingletonFluidBufferBlockData> {
    public FluidBufferBlockData(List<SingletonFluidBufferBlockData> data) {
        this.data = data;
    }

    @Override
    public Iterator<SingletonFluidBufferBlockData> iterator() {
        return data.iterator();
    }

    public Set<PylonFluid> inputFluids() {
        return data.stream().filter(SingletonFluidBufferBlockData::input).map(SingletonFluidBufferBlockData::fluid).collect(Collectors.toSet());
    }

    public Set<PylonFluid> outputFluids() {
        return data.stream().filter(SingletonFluidBufferBlockData::output).map(SingletonFluidBufferBlockData::fluid).collect(Collectors.toSet());
    }
}

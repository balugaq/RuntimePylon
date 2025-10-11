package com.balugaq.runtimepylon.config;

import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record PreparedFluid(
        RegisteredObjectID id,
        Material material,
        FluidTemperature temperature,
        boolean postLoad
) implements PostLoadable {
}

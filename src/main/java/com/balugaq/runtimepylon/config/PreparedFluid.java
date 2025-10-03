package com.balugaq.runtimepylon.config;

import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class PreparedFluid {
    private final Material material;
    private final FluidTemperature temperature;
}

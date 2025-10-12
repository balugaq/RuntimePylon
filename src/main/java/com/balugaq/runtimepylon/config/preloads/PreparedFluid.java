package com.balugaq.runtimepylon.config.preloads;

import com.balugaq.runtimepylon.config.PageDesc;
import com.balugaq.runtimepylon.config.PostLoadable;
import com.balugaq.runtimepylon.config.RegisteredObjectID;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public record PreparedFluid(
        RegisteredObjectID id,
        Material material,
        FluidTemperature temperature,
        @Nullable List<PageDesc> pages,
        boolean postLoad
) implements PostLoadable {
}

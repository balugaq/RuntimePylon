package com.balugaq.pc.config.preloads;

import com.balugaq.pc.config.PageDesc;
import com.balugaq.pc.config.PostLoadable;
import com.balugaq.pc.config.RegisteredObjectID;
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

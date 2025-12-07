package com.balugaq.runtimepylon.config;

import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Optional;

/**
 * @author balugaq
 */
@NullMarked
public record SingletonFluidBlockData(FluidPointType fluidPointType, BlockFace face,
                                      boolean allowVerticalFaces) implements Deserializer<SingletonFluidBlockData> {
    public SingletonFluidBlockData() {
        this(FluidPointType.INTERSECTION, BlockFace.NORTH, true);
    }

    @Override
    public List<ConfigReader<?, SingletonFluidBlockData>> readers() {
        return List.of(ConfigReader.of(
                ConfigurationSection.class, section -> {
                    FluidPointType type = Optional.ofNullable(Pack.readEnumOrNull(section, FluidPointType.class, "type"))
                            .orElse(FluidPointType.INTERSECTION);
                    BlockFace face = Optional.ofNullable(Pack.readEnumOrNull(section, BlockFace.class, "face"))
                            .orElse(BlockFace.NORTH);
                    if (!face.isCartesian()) {
                        throw new IllegalArgumentException("Expected cartesian blockfaces, but invalid face: " + face);
                    }
                    return new SingletonFluidBlockData(type, face, section.getBoolean("allowVerticalFaces", true));
                }
        ));
    }
}

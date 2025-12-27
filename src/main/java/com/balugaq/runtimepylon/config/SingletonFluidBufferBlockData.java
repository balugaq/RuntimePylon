package com.balugaq.runtimepylon.config;

import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public record SingletonFluidBufferBlockData(PylonFluid fluid, double capacity, boolean input,
                                            boolean output) implements Deserializer<SingletonFluidBufferBlockData> {
    public SingletonFluidBufferBlockData() {
        this(null, 0, true, true);
    }

    @Override
    public List<ConfigReader<?, SingletonFluidBufferBlockData>> readers() {
        return ConfigReader.list(
                ConfigurationSection.class, section -> {
                    PylonFluid fluid = Deserializer.PYLON_FLUID.deserialize(section.get("fluid"));
                    double capacity = section.getDouble("capacity", 0);
                    boolean input = section.getBoolean("input", false);
                    boolean output = section.getBoolean("output", false);
                    return new SingletonFluidBufferBlockData(fluid, capacity, input, output);
                }
        );
    }
}

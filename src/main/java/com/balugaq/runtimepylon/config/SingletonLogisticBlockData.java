package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
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
public record SingletonLogisticBlockData(String name, char invSlotChar) implements Deserializer<SingletonLogisticBlockData> {
    public SingletonLogisticBlockData() {
        this("", 'i');
    }

    @Override
    public List<ConfigReader<?, SingletonLogisticBlockData>> readers() {
        return ConfigReader.list(
                ConfigurationSection.class, section -> {
                    String name = section.getString("name");
                    if (name == null) throw new MissingArgumentException("name");
                    char invSlotChar = section.getString("invSlotChar", "i").charAt(0);
                    return new SingletonLogisticBlockData(name, invSlotChar);
                }
        );
    }
}

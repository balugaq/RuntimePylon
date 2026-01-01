package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import io.github.pylonmc.pylon.core.logistics.LogisticSlotType;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public record SingletonLogisticBlockData(String name, LogisticSlotType slotType, char invSlotChar) implements Deserializer<SingletonLogisticBlockData> {
    public SingletonLogisticBlockData() {
        this("", LogisticSlotType.INPUT, 'i');
    }

    @Override
    public List<ConfigReader<?, SingletonLogisticBlockData>> readers() {
        return ConfigReader.list(
                ConfigurationSection.class, section -> {
                    String name = section.getString("name");
                    if (name == null) throw new MissingArgumentException("name");
                    LogisticSlotType slotType = Deserializer.LOGISTIC_SLOT_TYPE.deserialize(section.get("type"));
                    char invSlotChar = section.getString("inv-slot-char", "i").charAt(0);
                    return new SingletonLogisticBlockData(name, slotType, invSlotChar);
                }
        );
    }
}

package com.balugaq.runtimepylon.config.register;

import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PackDesc;
import com.balugaq.runtimepylon.config.PluginDesc;
import com.balugaq.runtimepylon.exceptions.UnknownFlagException;
import com.balugaq.runtimepylon.util.MinecraftVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public interface RegisterCondition extends Deserializer<RegisterCondition> {
    boolean pass();

    default List<ConfigReader<?, RegisterCondition>> readers() {
        return List.of(
                ConfigReader.of(Boolean.class, BooleanRegisterCondition::new),
                ConfigReader.of(Number.class, NumberRegisterCondition::new),
                ConfigReader.of(String.class, StringRegisterCondition::new)
        );
    }

    /**
     * @author balugaq
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @NullMarked
    class BooleanRegisterCondition implements RegisterCondition {
        private boolean value;

        @Override
        public boolean pass() {
            return value;
        }
    }

    /**
     * @author balugaq
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @NullMarked
    class NumberRegisterCondition implements RegisterCondition {
        private Number value;

        @Override
        public boolean pass() {
            return value.intValue() != 0;
        }
    }

    /**
     * @author balugaq
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @NullMarked
    class StringRegisterCondition implements RegisterCondition {
        private String value;

        @Override
        public boolean pass() {
            if ("true".equalsIgnoreCase(value)) return true;
            if ("false".equalsIgnoreCase(value)) return false;
            try {
                return new NumberRegisterCondition(Integer.parseInt(value)).pass();
            } catch (NumberFormatException ignored) {
            }

            if (value.startsWith("version ")) {
                String[] ss = value.split(" ");
                if (ss.length < 3) throw new UnknownFlagException(value);
                String symbol = ss[1];
                MinecraftVersion version = MinecraftVersion.of(ss[2]);
                return switch (symbol) {
                    case ">=" -> MinecraftVersion.current().compareTo(version) >= 0;
                    case ">" -> MinecraftVersion.current().compareTo(version) > 0;
                    case "<=" -> MinecraftVersion.current().compareTo(version) <= 0;
                    case "<" -> MinecraftVersion.current().compareTo(version) < 0;
                    case "==" -> MinecraftVersion.current().equals(version);
                    case "!=" -> !MinecraftVersion.current().equals(version);
                    default -> throw new UnknownFlagException(value);
                };
            }

            if (value.startsWith("hasPack ")) {
                String packName = value.substring(8);
                PackDesc desc = Deserializer.PACK_DESC.deserialize(packName);
                Pack pack = desc.findPack();
                return pack != null;
            }

            if (value.startsWith("!hasPack ")) {
                String packName = value.substring(9);
                PackDesc desc = Deserializer.PACK_DESC.deserialize(packName);
                Pack pack = desc.findPack();
                return pack == null;
            }

            if (value.startsWith("hasPlugin ")) {
                String pluginName = value.substring(10);
                PluginDesc desc = Deserializer.PLUGIN_DESC.deserialize(pluginName);
                Plugin plugin = desc.findPlugin();
                return plugin != null;
            }

            if (value.startsWith("!hasPlugin ")) {
                String pluginName = value.substring(11);
                PluginDesc desc = Deserializer.PLUGIN_DESC.deserialize(pluginName);
                Plugin plugin = desc.findPlugin();
                return plugin == null;
            }

            throw new UnknownFlagException(value);
        }
    }
}

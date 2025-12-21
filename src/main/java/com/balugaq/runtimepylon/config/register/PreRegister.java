package com.balugaq.runtimepylon.config.register;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.exceptions.IncompatibleKeyFormatException;
import com.balugaq.runtimepylon.exceptions.InvalidDescException;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
@UtilityClass
public class PreRegister {
    @Nullable
    public static ConfigurationSection read(FileConfiguration config, String key) {
        if (!key.matches("[a-z0-9_\\-./]+")) throw new IncompatibleKeyFormatException(key);

        ConfigurationSection section = config.getConfigurationSection(key);
        if (section == null) throw new InvalidDescException(key);
        if (PreRegister.blocks(section)) return null;

        return section;
    }

    private static boolean blocks(ConfigurationSection section) {
        Object o = section.get("register-conditions");
        if (o != null) {
            RegisterConditions conditions = Deserializer.REGISTER_CONDITIONS.deserialize(o);
            return !conditions.pass();
        }
        return false;
    }
}

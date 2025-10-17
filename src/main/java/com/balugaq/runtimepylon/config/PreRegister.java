package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.config.register.RegisterConditions;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author balugaq
 */
public class PreRegister {
    public static boolean blocks(ConfigurationSection section) {
        Object o = section.get("register-conditions");
        if (o != null) {
            RegisterConditions conditions = Deserializer.newDeserializer(RegisterConditions.class).deserialize(o);
            return !conditions.pass();
        }
        return false;
    }
}

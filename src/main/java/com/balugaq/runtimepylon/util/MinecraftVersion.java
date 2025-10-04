package com.balugaq.runtimepylon.util;

import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.exceptions.DeserializationException;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Minecraft versions enum for version judgement
 *
 * @author lijinhong11
 */
@AllArgsConstructor
@NullMarked
public enum MinecraftVersion implements Deserializer<MinecraftVersion> {
    V1_21_8(1, 21, 8),
    V1_21_9(1, 21, 9),
    V1_21_10(1, 21, 10),
    V1_21_11(1, 21, 11),
    V1_22_0(1, 22, 0),
    V1_22_1(1, 22, 1),
    UNKNOWN(999, 999, 999);

    private final int major;
    private final int minor;
    private final int patch;

    public static MinecraftVersion find(int major, int minor, int patch) {
        for (var version : values()) {
            if (version.major == major &&
                    version.minor == minor &&
                    version.patch == patch) {
                return version;
            }
        }

        throw new DeserializationException("Unknown Minecraft version: " + major + "." + minor + "." + patch);
    }

    public boolean isAtLeast(String version) {
        return isAtLeast(deserialize(version));
    }

    public boolean isAtLeast(MinecraftVersion version) {
        return this.major > version.major ||
                this.major == version.major && this.minor > version.minor ||
                this.major == version.major && this.minor == version.minor && this.patch >= version.patch;
    }

    public boolean isBefore(String version) {
        return isBefore(deserialize(version));
    }

    public boolean isBefore(MinecraftVersion version) {
        return !isAtLeast(version) && this != version;
    }

    @Override
    public List<ConfigReader<?, MinecraftVersion>> readers() {
        return List.of(
                ConfigReader.of(String.class, s -> {
                    String[] split = s.split("\\.");
                    int major = Integer.parseUnsignedInt(split[0]);
                    int minor = Integer.parseUnsignedInt(split[1]);
                    int patch = Integer.parseUnsignedInt(split.length == 2 ? "0" : split[2]);
                    return MinecraftVersion.find(major, minor, patch);
                })
        );
    }
}

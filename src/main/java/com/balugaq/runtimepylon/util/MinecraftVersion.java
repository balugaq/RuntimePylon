package com.balugaq.runtimepylon.util;

import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.exceptions.InvalidDescException;
import org.bukkit.Bukkit;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Minecraft versions for version judgement
 *
 * @author lijinhong11
 * @author balugaq
 */
@NullMarked
public record MinecraftVersion(int major, int minor, int patch) implements Deserializer<MinecraftVersion>, Comparable<MinecraftVersion> {
    public static final MinecraftVersion UNKNOWN = MinecraftVersion.of(999, 999, 999);

    public static MinecraftVersion current() {
        return MinecraftVersion.of(Bukkit.getMinecraftVersion());
    }

    public static MinecraftVersion of(String version) {
        return MinecraftVersion.UNKNOWN.deserialize(version);
    }

    public static MinecraftVersion of(int major, int minor, int patch) {
        return new MinecraftVersion(major, minor, patch);
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
                    if (split.length < 2) throw new InvalidDescException("Invalid version string: " + s);
                    int major = Integer.parseUnsignedInt(split[0]);
                    int minor = Integer.parseUnsignedInt(split[1]);
                    int patch = Integer.parseUnsignedInt(split.length == 2 ? "0" : split[2]);
                    return MinecraftVersion.of(major, minor, patch);
                })
        );
    }

    @Override
    public int compareTo(MinecraftVersion o) {
        return Integer.compare(this.major * 1000000 + this.minor * 10000 + this.patch, o.major * 1000000 + o.minor * 10000 + o.patch);
    }
}

package com.balugaq.pc.util;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class OSUtil {
    private static final OSType CURRENT_OS = getOSType();
    private static final ArchType CURRENT_ARCH = getArchType();

    private static OSType getOSType() {
        String osName = System.getProperty("os.name", "unknown").toLowerCase();

        if (osName.contains("win")) {
            return OSType.WINDOWS;
        } else if (osName.contains("mac") || osName.contains("os x")) {
            return OSType.MAC_OS;
        } else if (osName.contains("nux") || osName.contains("linux")) {
            return OSType.LINUX;
        } else {
            return OSType.UNKNOWN;
        }
    }

    public static boolean isWindows() {
        return CURRENT_OS == OSType.WINDOWS;
    }

    public static boolean isLinux() {
        return CURRENT_OS == OSType.LINUX;
    }

    public static boolean isMac() {
        return CURRENT_OS == OSType.MAC_OS;
    }

    private static ArchType getArchType() {
        String osArch = System.getProperty("os.arch", "unknown").toLowerCase();
        String dataModel = System.getProperty("sun.arch.data.model", "unknown");

        if (osArch.contains("arm")) {
            if (osArch.contains("64") || osArch.contains("aarch64") || "64".equals(dataModel)) {
                return ArchType.ARM_64;
            } else {
                return ArchType.ARM_32;
            }
        } else if (osArch.contains("x86") || osArch.contains("i386") || osArch.contains("i686")) {
            return ArchType.X86_32;
        } else if (osArch.contains("amd64") || osArch.contains("x86_64")) {
            return ArchType.X86_64;
        } else {
            return ArchType.UNKNOWN;
        }
    }

    public static boolean isX86() {
        return CURRENT_ARCH == ArchType.X86_32 || CURRENT_ARCH == ArchType.X86_64;
    }

    public static boolean isARM() {
        return CURRENT_ARCH == ArchType.ARM_32 || CURRENT_ARCH == ArchType.ARM_64;
    }

    public static boolean isX86_64() {
        return CURRENT_ARCH == ArchType.X86_64;
    }

    public static boolean isARM_64() {
        return CURRENT_ARCH == ArchType.ARM_64;
    }

    private enum OSType {
        WINDOWS, LINUX, MAC_OS, UNKNOWN
    }

    private enum ArchType {
        X86_32, X86_64, ARM_32, ARM_64, UNKNOWN
    }
}

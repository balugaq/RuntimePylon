package com.balugaq.runtimepylon.util;

import com.balugaq.runtimepylon.DebuggablePlugin;
import com.balugaq.runtimepylon.RuntimePylon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"unused", "deprecation", "CallToPrintStackTrace", "ResultOfMethodCallIgnored"})
@NullMarked
public class Debug {
    private static final String DEBUG_PREFIX = "[Debug] ";
    private static @Nullable DebuggablePlugin plugin = null;

    public static void dumpStack() {
        Thread.dumpStack();
    }

    public static void traceExactly(Throwable e, @Nullable String doing, @Nullable Integer code) {
        try {
            severe("====================AN FATAL OCCURRED" + (doing != null ? (" WHEN " + doing.toUpperCase()) : "") + "====================");
            severe("DO NOT REPORT THIS ERROR TO " + getPlugin().getName() + " DEVELOPERS!!! THIS IS NOT A " + getPlugin().getName() + " BUG!");
            if (code != null) {
                getPlugin().getLogger().severe("Error code: " + code);
            }
            severe("If you are sure that this is a " + getPlugin().getName() + " bug, please report to " + getPlugin().getIssueTrackerLink());
            getPlugin().getLogger().severe("An unexpected error occurred" + (doing != null ? (" while " + doing) : "."));

            e.printStackTrace();

            getPlugin().getLogger().severe("ALL EXCEPTION INFORMATION IS BELOW:");
            getPlugin().getLogger().severe("message: " + e.getMessage());
            getPlugin().getLogger().severe("localizedMessage: " + e.getLocalizedMessage());
            getPlugin().getLogger().severe("cause: " + e.getCause());
            getPlugin().getLogger().severe("stackTrace: " + Arrays.toString(e.getStackTrace()));
            getPlugin().getLogger().severe("suppressed: " + Arrays.toString(e.getSuppressed()));

            dumpToFile(e, code);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }

    public static void severe(String message) {
        RuntimePylon.getInstance().getLogger().severe(message);
    }

    public static DebuggablePlugin getPlugin() {
        if (plugin == null) {
            plugin = RuntimePylon.getInstance();
        }
        return plugin;
    }

    public static void dumpToFile(Throwable e, @Nullable Integer code) {
        // Format as: yyyy-MM-dd-HH-mm-ss-e.getClass().getSimpleName()-uuid
        String fileName = "error-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
                + "-" + e.getClass().getSimpleName() + "-" + UUID.randomUUID() + ".txt";

        File file = new File(getPlugin().getErrorReportsFolder(), fileName);
        try {
            file.createNewFile();
            try (PrintStream stream = new PrintStream(file, StandardCharsets.UTF_8)) {
                stream.println("====================AN FATAL OCCURRED====================");
                stream.println(
                        "DO NOT REPORT THIS ERROR TO " + getPlugin().getName() + " DEVELOPERS!!! THIS IS NOT A " + getPlugin().getName() + " BUG!");
                stream.println("If you are sure that this is a " + getPlugin().getName() + " bug, please report to https://github.com/balugaq/RuntimePylon/issues");
                stream.println("An unexpected error occurred.");
                stream.println(getPlugin().getName() + " version: "
                                       + RuntimePylon.getInstance().getDescription().getVersion());
                stream.println("Java version: " + System.getProperty("java.version"));
                stream.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " "
                                       + System.getProperty("os.arch"));
                stream.println("Minecraft version: " + Bukkit.getMinecraftVersion());
                stream.println("PylonCore version: " + Bukkit.getPluginManager().getPlugin("PylonCore").getPluginMeta().getVersion());
                if (code != null) {
                    stream.println("Error code: " + code);
                }
                stream.println("Error: " + e);
                stream.println("Stack trace:");
                e.printStackTrace(stream);

                warning("");
                warning("An Error occurred! It has been saved as: ");
                warning("/plugins/" + getPlugin().getName() + "/error-reports/" + file.getName());
                warning("Please put this file on https://mclo.gs/ and report this to the developer(s).");

                warning("Please DO NOT send screenshots of these logs to the developer(s).");
                warning("");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void warning(String message) {
        RuntimePylon.getInstance().getLogger().warning(message);
    }

    public static void severe(Object... objects) {
        severe(Arrays.toString(objects));
    }

    public static void severe(Throwable e) {
        RuntimePylon.getInstance().getLogger().severe(e.getMessage());
        trace(e);
    }

    public static void trace(Throwable e) {
        trace(e, null);
    }

    public static void trace(Throwable e, @Nullable String doing) {
        trace(e, doing, null);
    }

    public static void trace(Throwable e, @Nullable String doing, @Nullable Integer code) {
        try {
            severe("DO NOT REPORT THIS ERROR TO " + getPlugin().getName() + " DEVELOPERS!!! THIS IS NOT A " + getPlugin().getName() + " BUG!");
            if (code != null) {
                getPlugin().getLogger().severe("Error code: " + code);
            }
            severe("If you are sure that this is a " + getPlugin().getName() + " bug, please report to " + getPlugin().getIssueTrackerLink());
            getPlugin().getLogger().severe("An unexpected error occurred" + (doing != null ? " while " + doing : "."));

            e.printStackTrace();

            dumpToFile(e, code);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }

    public static void severe(String... messages) {
        for (String message : messages) {
            warning(message);
        }
    }

    public static void warning(Object... objects) {
        warning(Arrays.toString(objects));
    }

    public static void warning(Throwable e) {
        warning(e.getMessage());
        trace(e);
    }

    public static void debug(Object... objects) {
        debug(Arrays.toString(objects));
    }

    public static void debug(String message) {
        if (RuntimePylon.getConfigManager().isDebug()) {
            log(DEBUG_PREFIX + message);
        }
    }

    public static void log(String message) {
        RuntimePylon.getInstance().getLogger().info(message);
    }

    public static void debug(Throwable e) {
        debug(e.getMessage());
        trace(e);
    }

    public static void debug(String... messages) {
        for (String message : messages) {
            debug(message);
        }
    }

    public static void sendMessage(Player player, Object... objects) {
        sendMessage(player, Arrays.toString(objects));
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage("[" + getPlugin().getName() + "]" + message);
    }

    public static void log(Object... object) {
        log(Arrays.toString(object));
    }

    public static void log(Throwable e) {
        Debug.trace(e);
    }

    public static void log() {
        log("");
    }
}

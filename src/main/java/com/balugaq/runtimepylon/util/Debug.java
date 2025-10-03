package com.balugaq.runtimepylon.util;

import com.balugaq.runtimepylon.RuntimePylon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
@SuppressWarnings({"unused", "deprecation", "CallToPrintStackTrace", "ResultOfMethodCallIgnored", "JavaExistingMethodCanBeUsed"})
public class Debug {
    public static final File errorsFolder =
            new File(RuntimePylon.getInstance().getDataFolder(), "error-reports");
    private static final String debugPrefix = "[Debug] ";
    private static @Nullable JavaPlugin plugin = null;

    static {
        if (!errorsFolder.exists()) {
            errorsFolder.mkdirs();
        }
    }

    @NotNull
    public static JavaPlugin getPlugin() {
        if (plugin == null) {
            plugin = RuntimePylon.getInstance();
        }
        return plugin;
    }

    public static void severe(Object @NotNull ... objects) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : objects) {
            if (obj == null) {
                sb.append("null");
            } else {
                sb.append(obj);
            }
            sb.append(" ");
        }
        warn(sb.toString());
    }

    public static void severe(@NotNull Throwable e) {
        warn(e.getMessage());
        trace(e);
    }

    public static void severe(@Nullable Object object) {
        warn(object == null ? "null" : object.toString());
    }

    public static void severe(String @NotNull ... messages) {
        for (String message : messages) {
            warn(message);
        }
    }

    public static void severe(String message) {
        log("&e[ERROR] " + message);
    }

    public static void warn(Object @NotNull ... objects) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : objects) {
            if (obj == null) {
                sb.append("null");
            } else {
                sb.append(obj);
            }
            sb.append(" ");
        }
        warn(sb.toString());
    }

    public static void warn(@NotNull Throwable e) {
        warn(e.getMessage());
        trace(e);
    }

    public static void warn(@Nullable Object object) {
        warn(object == null ? "null" : object.toString());
    }

    public static void warn(String @NotNull ... messages) {
        for (String message : messages) {
            warn(message);
        }
    }

    public static void warn(String message) {
        log("&e[WARN] " + message);
    }

    public static void debug(Object @NotNull ... objects) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : objects) {
            if (obj == null) {
                sb.append("null");
            } else {
                sb.append(obj);
            }
            sb.append(" ");
        }
        debug(sb.toString());
    }

    public static void debug(@NotNull Throwable e) {
        debug(e.getMessage());
        trace(e);
    }

    public static void debug(@Nullable Object object) {
        debug(object == null ? "null" : object.toString());
    }

    public static void debug(String @NotNull ... messages) {
        for (String message : messages) {
            debug(message);
        }
    }

    public static void debug(String message) {
        if (RuntimePylon.getConfigManager().isDebug()) {
            log(debugPrefix + message);
        }
    }

    public static void sendMessage(@NotNull Player player, Object @NotNull ... objects) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : objects) {
            if (obj == null) {
                sb.append("null");
            } else {
                sb.append(obj);
            }
            sb.append(" ");
        }
        sendMessage(player, sb.toString());
    }

    public static void sendMessage(@NotNull Player player, @Nullable Object object) {
        if (object == null) {
            sendMessage(player, "null");
            return;
        }
        sendMessage(player, object.toString());
    }

    public static void sendMessages(@NotNull Player player, String @NotNull ... messages) {
        for (String message : messages) {
            sendMessage(player, message);
        }
    }

    public static void sendMessage(@NotNull Player player, String message) {
        player.sendMessage("[" + getPlugin().getName() + "]" + message);
    }

    public static void dumpStack() {
        Thread.dumpStack();
    }

    public static void log(Object @NotNull ... object) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : object) {
            if (obj == null) {
                sb.append("null");
            } else {
                sb.append(obj);
            }
            sb.append(" ");
        }

        log(sb.toString());
    }

    public static void log(@Nullable Object object) {
        log(object == null ? "null" : object.toString());
    }

    public static void log(String @NotNull ... messages) {
        for (String message : messages) {
            log(message);
        }
    }

    public static void log(@NotNull String message) {
        Bukkit.getServer()
                .getConsoleSender()
                .sendMessage("[" + RuntimePylon.getInstance().getName() + "] " + ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void log(@NotNull Throwable e) {
        Debug.trace(e);
    }

    public static void log() {
        log("");
    }

    public static void trace(@NotNull Throwable e) {
        trace(e, null);
    }

    public static void trace(@NotNull Throwable e, @Nullable String doing) {
        trace(e, doing, null);
    }

    public static void trace(@NotNull Throwable e, @Nullable String doing, @Nullable Integer code) {
        try {
            getPlugin()
                    .getLogger()
                    .severe(
                            "DO NOT REPORT THIS ERROR TO RuntimePylon DEVELOPERS!!! THIS IS NOT A RuntimePylon BUG!");
            if (code != null) {
                getPlugin().getLogger().severe("Error code: " + code);
            }
            getPlugin()
                    .getLogger()
                    .severe("If you are sure that this is a RuntimePylon bug, please report to https://github.com/balugaq/RuntimePylon/issues");
            if (doing != null) {
                getPlugin().getLogger().severe("An unexpected error occurred while " + doing);
            } else {
                getPlugin().getLogger().severe("An unexpected error occurred.");
            }

            e.printStackTrace();

            dumpToFile(e, code);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }

    public static void traceExactly(@NotNull Throwable e, @Nullable String doing, @Nullable Integer code) {
        try {
            getPlugin()
                    .getLogger()
                    .severe("====================AN FATAL OCCURRED"
                            + (doing != null ? (" WHEN " + doing.toUpperCase()) : "") + "====================");
            getPlugin()
                    .getLogger()
                    .severe(
                            "DO NOT REPORT THIS ERROR TO RuntimePylon DEVELOPERS!!! THIS IS NOT A RuntimePylon BUG!");
            if (code != null) {
                getPlugin().getLogger().severe("Error code: " + code);
            }
            getPlugin()
                    .getLogger()
                    .severe("If you are sure that this is a RuntimePylon bug, please report to https://github.com/balugaq/RuntimePylon/issues");
            if (doing != null) {
                getPlugin().getLogger().severe("An unexpected error occurred while " + doing);
            } else {
                getPlugin().getLogger().severe("An unexpected error occurred.");
            }

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

    public static void dumpToFile(@NotNull Throwable e, @Nullable Integer code) {
        // Format as: yyyy-MM-dd-HH-mm-ss-e.getClass().getSimpleName()-uuid
        String fileName = "error-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
                + "-" + e.getClass().getSimpleName() + "-" + UUID.randomUUID() + ".txt";

        File file = new File(errorsFolder, fileName);
        try {
            file.createNewFile();
            try (PrintStream stream = new PrintStream(file, StandardCharsets.UTF_8)) {
                stream.println("====================AN FATAL OCCURRED====================");
                stream.println(
                        "DO NOT REPORT THIS ERROR TO RuntimePylon DEVELOPERS!!! THIS IS NOT A RuntimePylon BUG!");
                stream.println("If you are sure that this is a RuntimePylon bug, please report to https://github.com/balugaq/RuntimePylon/issues");
                stream.println("An unexpected error occurred.");
                stream.println("RuntimePylon version: "
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

                warn("");
                warn("An Error occurred! It has been saved as: ");
                warn("/plugins/RuntimePylon/error-reports/" + file.getName());
                warn("Please put this file on https://pastebin.com/ and report this to the developer(s).");

                warn("Please DO NOT send screenshots of these logs to the developer(s).");
                warn("");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

package com.balugaq.runtimepylon;

import com.balugaq.runtimepylon.command.RuntimePylonCommand;
import com.balugaq.runtimepylon.config.StackFormatter;
import com.balugaq.runtimepylon.listener.ChatInputListener;
import com.balugaq.runtimepylon.manager.ConfigManager;
import com.balugaq.runtimepylon.manager.IntegrationManager;
import com.balugaq.runtimepylon.manager.PackManager;
import com.balugaq.runtimepylon.util.Debug;
import com.balugaq.runtimepylon.util.OSUtil;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.button.PageButton;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author balugaq
 */
@NullMarked
public class RuntimePylon extends JavaPlugin implements PylonAddon, DebuggablePlugin {
    @Getter
    @UnknownNullability
    private static RuntimePylon instance;
    private final Set<Locale> SUPPORTED_LANGUAGES = new HashSet<>();
    @UnknownNullability
    private ConfigManager configManager;
    @UnknownNullability
    private IntegrationManager integrationManager;
    @UnknownNullability
    private PackManager packManager;

    public static Map<NamespacedKey, PageButton> getPageButtons() {
        Map<NamespacedKey, PageButton> pages = new HashMap<>(PylonGuide.getRootPage().getButtons()
          .stream()
          .filter(button -> button instanceof PageButton)
          .map(button -> (PageButton) button)
          .collect(Collectors.toMap(
                  button -> button.getPage().getKey(),
                  button -> button,
                  (a, b) -> b
          )));
        pages.putAll(GlobalVars.getCustomPages());
        return pages;
    }

    private static void scanPages(Map<NamespacedKey, SimpleStaticGuidePage> pages, SimpleStaticGuidePage page) {
        for (var e : page.getButtons()) {
            if (e instanceof PageButton p) {
                var p2 = p.getPage();
                if (p2 instanceof SimpleStaticGuidePage p3) {
                    pages.put(p2.getKey(), p3);
                    scanPages(pages, p3);
                }
            }
        }
    }

    public static Map<NamespacedKey, SimpleStaticGuidePage> getPages() {
        Map<NamespacedKey, SimpleStaticGuidePage> pages = new HashMap<>();
        scanPages(pages, PylonGuide.getRootPage());
        for (var e : GlobalVars.getCustomPages().entrySet()) {
            if (e.getValue().getPage() instanceof SimpleStaticGuidePage p) {
                pages.put(e.getKey(), p);
            }
        }
        pages.put(PylonGuide.getRootPage().getKey(), PylonGuide.getRootPage());
        return pages;
    }

    public static void runTaskLater(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(getInstance(), runnable, delay);
    }

    public static void runTaskAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(getInstance(), runnable);
    }

    public static void runTaskLaterAsync(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(getInstance(), runnable, delay);
    }

    public static void runTaskTimerAsync(Runnable runnable, long delay, long period) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(getInstance(), runnable, delay, period);
    }

    public static void runTaskTimerAsync(Consumer<? super BukkitTask> task, long delay, long period) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(getInstance(), task, delay, period);
    }

    public static void runTaskTimer(Runnable runnable, long delay, long period) {
        Bukkit.getScheduler().runTaskTimer(getInstance(), runnable, delay, period);
    }

    public static void runTaskTimer(Consumer<? super BukkitTask> consumer, long delay, long period) {
        Bukkit.getScheduler().runTaskTimer(getInstance(), consumer, delay, period);
    }

    public static ConfigManager getConfigManager() {
        return instance.configManager;
    }

    public static IntegrationManager getIntegrationManager() {
        return instance.integrationManager;
    }

    public static PackManager getPackManager() {
        return instance.packManager;
    }

    @Override
    public void onEnable() {
        // `/runtime updatepacks` to update packs from github // todo
        instance = this;
        addSupportedLanguages(Locale.ENGLISH);
        setupLibraries();

        // registerWithPylon();
        PylonRegistry.ADDONS.register(this);

        saveDefaultConfig();

        configManager = new ConfigManager(this);
        integrationManager = new IntegrationManager();
        packManager = new PackManager();

        // deprecated
        //RuntimeItems.initialize();
        //RuntimeBlocks.initialize();
        runTaskLater(() -> {
            try (var ignored = StackFormatter.setPosition("Loading packs")) {
                packManager.loadPacks();
            } catch (Exception e) {
                StackFormatter.handle(e);
            }
        }, 2L); // wait plugin dependencies load

        getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS, commands -> {
                    commands.registrar().register(RuntimePylonCommand.ROOT);
                }
        );

        Bukkit.getServer().getPluginManager().registerEvents(new ChatInputListener(), this);

        PylonRegistry.ADDONS.unregister(this);
        registerWithPylon();
    }

    @Override
    public void onDisable() {
        RuntimePylon.getPackManager().destroy();
    }

    private void setupLibraries() {
        BukkitLibraryManager libraryManager = new BukkitLibraryManager(this);

        List<String> repos = List.of(
                "https://maven.aliyun.com/repository/public",
                "https://repo.papermc.io/repository/maven-public/",
                "https://central.sonatype.com/repository/maven-snapshots/",
                "https://jitpack.io"
        );
        
        for (String repo : repos) {
            libraryManager.addRepository(repo);
        }

        libraryManager.addMavenCentral();
        
        String javetVersion = "5.0.2";
        List<String> dependencies = new ArrayList<>();
        dependencies.add("javet");
        if (OSUtil.isWindows()) {
            dependencies.add("javet-node-windows-x86_64");
            dependencies.add("javet-v8-windows-x86_64");
        } else if (OSUtil.isLinux()) {
            if (OSUtil.isARM()) {
                dependencies.add("javet-node-linux-arm64");
                dependencies.add("javet-v8-linux-arm64");
            } else if (OSUtil.isX86_64()) {
                dependencies.add("javet-node-linux-x86_64");
                dependencies.add("javet-v8-linux-x86_64");
            }
        } else if (OSUtil.isMac()) {
            if (OSUtil.isARM()) {
                dependencies.add("javet-node-macos-arm64");
                dependencies.add("javet-v8-macos-arm64");
            } else if (OSUtil.isX86_64()) {
                dependencies.add("javet-node-macos-x86_64");
                dependencies.add("javet-v8-macos-x86_64");
            }
        }

        Debug.log("Downloading may take 5 minutes, please wait...");
        for (String dependency : dependencies) {
            loadLibrary(libraryManager, "com.caoccao.javet", dependency, javetVersion);
        }

        loadLibrary(libraryManager, "org.apache.httpcomponents", "httpclient", "4.5.14");
    }

    @SuppressWarnings("SameParameterValue")
    private void loadLibrary(LibraryManager libraryManager, String groupId, String artifactId, String version) {
        Library library = Library.builder()
                .groupId(groupId)
                .artifactId(artifactId)
                .version(version)
                .build();

        Debug.log("Downloading library: " + library);
        try {
            libraryManager.loadLibrary(library);
            Debug.log("Downloaded library: " + library);
        } catch (Exception e) {
            Debug.trace(e);
        }
    }

    public void addSupportedLanguages(Locale languages) {
        SUPPORTED_LANGUAGES.add(languages);
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return instance;
    }

    @Override
    public Set<Locale> getLanguages() {
        return SUPPORTED_LANGUAGES;
    }

    @Override
    public Material getMaterial() {
        return Material.COPPER_INGOT;
    }

    public void addSupportedLanguages(Set<Locale> languages) {
        SUPPORTED_LANGUAGES.addAll(languages);
    }

    @Override
    public String getRepoOwner() {
        return "balugaq";
    }

    @Override
    public String getRepoName() {
        return getName();
    }

    public static File getErrorReportsFolder() {
        return GlobalVars.getErrorReportsFolder();
    }

    public static File getPackUpdateDownloadFolder() {
        return GlobalVars.getPackUpdateDownloadFolder();
    }

    public static File getPacksFolder() {
        return GlobalVars.getPacksFolder();
    }
}

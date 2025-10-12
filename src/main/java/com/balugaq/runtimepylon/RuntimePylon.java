package com.balugaq.runtimepylon;

import com.balugaq.runtimepylon.command.RuntimePylonCommand;
import com.balugaq.runtimepylon.config.PackManager;
import com.balugaq.runtimepylon.listener.ChatInputListener;
import com.balugaq.runtimepylon.manager.ConfigManager;
import com.balugaq.runtimepylon.manager.IntegrationManager;
import com.balugaq.runtimepylon.pylon.RuntimeBlocks;
import com.balugaq.runtimepylon.pylon.RuntimeItems;
import com.balugaq.runtimepylon.util.Debug;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.button.PageButton;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RuntimePylon extends JavaPlugin implements PylonAddon {

    @Getter
    private static RuntimePylon instance;
    public Map<NamespacedKey, SimpleStaticGuidePage> customPages = new HashMap<>();
    private ConfigManager configManager;
    private IntegrationManager integrationManager;
    private PackManager packManager;

    @NotNull
    public static Map<NamespacedKey, SimpleStaticGuidePage> getGuidePages() {
        var pages = new HashMap<>(PylonGuide.getRootPage().getButtons()
                .stream()
                .filter(button -> button instanceof PageButton)
                .map(button -> ((PageButton) button).getPage())
                .filter(page -> page instanceof SimpleStaticGuidePage)
                .map(page -> (SimpleStaticGuidePage) page)
                .collect(Collectors.toMap(
                        Keyed::getKey,
                        page -> page,
                        (a, b) -> b
                )));
        pages.putAll(RuntimePylon.instance.customPages);
        return pages;
    }

    public static void runTaskLater(@NotNull Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(getInstance(), runnable, delay);
    }

    public static void runTaskAsyncLater(@NotNull Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(getInstance(), runnable, delay);
    }

    @NotNull
    public static ConfigManager getConfigManager() {
        return instance.configManager;
    }

    @NotNull
    public static IntegrationManager getIntegrationManager() {
        return instance.integrationManager;
    }

    @NotNull
    public static PackManager getPackManager() {
        return instance.packManager;
    }

    public void registerCustomPage(@NotNull SimpleStaticGuidePage page) {
        customPages.put(page.getKey(), page);
    }

    public void unregisterCustomPage(@NotNull SimpleStaticGuidePage page) {
        customPages.remove(page.getKey());
    }

    @Override
    public void onEnable() {
        // `/runtime updatepacks` to update packs from github // todo
        // `/runtime clearsettings` to clear settings
        // `/runtime clearrecipes` to clear recipes
        // `/runtime clearlang` to clear lang files
        // `/runtime clearall` to clear all
        // `/runtime reloadpacks` to reload packs
        instance = this;

        // registerWithPylon();
        PylonRegistry.ADDONS.register(this);

        saveDefaultConfig();

        configManager = new ConfigManager(this);
        integrationManager = new IntegrationManager();
        packManager = new PackManager();

        RuntimeItems.initialize();
        RuntimeBlocks.initialize();
        try {
            packManager.loadPacks();
        } catch (Exception e) {
            Debug.trace(e);
        }

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(RuntimePylonCommand.ROOT);
        });

        Bukkit.getServer().getPluginManager().registerEvents(new ChatInputListener(), this);

        PylonRegistry.ADDONS.unregister(this);
        registerWithPylon(); // todo: rewrite lang translation check
    }

    @Override
    public @NotNull JavaPlugin getJavaPlugin() {
        return instance;
    }

    @Override
    public @NotNull Set<@NotNull Locale> getLanguages() {
        return Set.of(
                Locale.ENGLISH,
                Locale.SIMPLIFIED_CHINESE
        );
    }

    @Override
    public @NotNull Material getMaterial() {
        return Material.COPPER_INGOT;
    }

    @Override
    public boolean suppressAddonNameWarning() {
        return true;
    }
}

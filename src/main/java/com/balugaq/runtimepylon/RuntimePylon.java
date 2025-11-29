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
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author balugaq
 */
@NullMarked
public class RuntimePylon extends JavaPlugin implements PylonAddon {
    @Getter
    @UnknownNullability
    private static RuntimePylon instance;
    private final Map<NamespacedKey, SimpleStaticGuidePage> customPages = new HashMap<>();
    private final Set<Locale> SUPPORTED_LANGUAGES = new HashSet<>();
    @UnknownNullability
    private ConfigManager configManager;
    @UnknownNullability
    private IntegrationManager integrationManager;
    @UnknownNullability
    private PackManager packManager;

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

    public static void runTaskLater(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(getInstance(), runnable, delay);
    }

    public static void runTaskAsyncLater(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(getInstance(), runnable, delay);
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

    public void registerCustomPage(SimpleStaticGuidePage page) {
        customPages.put(page.getKey(), page);
    }

    public void unregisterCustomPage(SimpleStaticGuidePage page) {
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
        addSupportedLanguages(Locale.ENGLISH);

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

        getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS, commands -> {
                    commands.registrar().register(RuntimePylonCommand.ROOT);
                }
        );

        Bukkit.getServer().getPluginManager().registerEvents(new ChatInputListener(), this);

        PylonRegistry.ADDONS.unregister(this);
        registerWithPylon(); // todo: rewrite lang translation check
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
}

//todo recipe_type
//todo research

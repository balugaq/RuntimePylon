package com.balugaq.runtimepylon;

import com.balugaq.runtimepylon.config.PackManager;
import com.balugaq.runtimepylon.manager.ConfigManager;
import com.balugaq.runtimepylon.manager.IntegrationManager;
import com.balugaq.runtimepylon.pylon.RuntimeBlocks;
import com.balugaq.runtimepylon.pylon.RuntimeItems;
import com.balugaq.runtimepylon.listener.ChatInputListener;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.button.PageButton;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
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

    private ConfigManager configManager;

    private IntegrationManager integrationManager;

    private PackManager packManager;

    public Map<NamespacedKey, SimpleStaticGuidePage> customPages = new HashMap<>();

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

    public void registerCustomPage(@NotNull SimpleStaticGuidePage page) {
        customPages.put(page.getKey(), page);
    }

    public void unregisterCustomPage(@NotNull SimpleStaticGuidePage page) {
        customPages.remove(page.getKey());
    }

    public static void runTaskLater(@NotNull Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(getInstance(), runnable, delay);
    }

    @Override
    public void onEnable() {
        // todo: /runtime commands
        // `/runtime updatepacks` to update packs from github
        // `/runtime clearsettings` to remerge settings
        // `/runtime clearrecipes` to remerge recipes
        // `/runtime clearlang` to remerge lang files
        instance = this;

        registerWithPylon();

        saveDefaultConfig();

        configManager = new ConfigManager(this);
        integrationManager = new IntegrationManager();
        packManager = new PackManager();

        RuntimeItems.initialize();
        RuntimeBlocks.initialize();
        packManager.loadPacks();

        Bukkit.getServer().getPluginManager().registerEvents(new ChatInputListener(), this);
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
}

package com.balugaq.runtimepylon;

import com.balugaq.runtimepylon.block.ItemHub;
import com.balugaq.runtimepylon.input.ChatInputListener;
import com.balugaq.runtimepylon.util.Key;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import io.github.pylonmc.pylon.core.block.PylonBlock;
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

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RuntimePylon extends JavaPlugin implements PylonAddon {

    @Getter
    private static RuntimePylon instance;

    @Override
    public void onEnable() {
        instance = this;

        registerWithPylon();

        saveDefaultConfig();

        PylonBlock.register(Key.create("item_hub"), Material.PURPUR_PILLAR, ItemHub.class);
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

    @NotNull
    public static Map<NamespacedKey, SimpleStaticGuidePage> getGuidePages() {
        return PylonGuide.getRootPage().getButtons()
                .stream()
                .filter(button -> button instanceof PageButton)
                .map(button -> ((PageButton) button).getPage())
                .filter(page -> page instanceof SimpleStaticGuidePage)
                .map(page -> (SimpleStaticGuidePage) page)
                .collect(Collectors.toMap(
                        Keyed::getKey,
                        page -> page,
                        (a, b) -> b
                ));
    }
}

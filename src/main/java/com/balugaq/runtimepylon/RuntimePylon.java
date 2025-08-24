package com.balugaq.runtimepylon;

import com.balugaq.runtimepylon.block.FluidHub;
import com.balugaq.runtimepylon.block.ItemHub;
import com.balugaq.runtimepylon.block.PageHub;
import com.balugaq.runtimepylon.input.ChatInputListener;
import com.balugaq.runtimepylon.item.NumberStack;
import com.balugaq.runtimepylon.item.StringStack;
import com.balugaq.runtimepylon.item.fluid.FluidTemperatureHolder;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.button.PageButton;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
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

    public static void runTaskLater(@NotNull Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(getInstance(), runnable, delay);
    }

    @Override
    public void onEnable() {
        instance = this;

        registerWithPylon();

        saveDefaultConfig();

        PylonItem.register(
                PylonItem.class,
                ItemStackBuilder.pylonItem(
                        Material.PURPUR_PILLAR,
                        RuntimeKeys.item_hub
                ).build(),
                RuntimeKeys.item_hub // block signature
        );
        RuntimePages.MAIN.addItem(RuntimeKeys.item_hub);

        PylonItem.register(
                PylonItem.class,
                ItemStackBuilder.pylonItem(
                        Material.LAPIS_BLOCK,
                        RuntimeKeys.fluid_hub
                ).build(),
                RuntimeKeys.fluid_hub
        );
        RuntimePages.MAIN.addItem(RuntimeKeys.fluid_hub);

        PylonItem.register(
                PylonItem.class,
                ItemStackBuilder.pylonItem(
                        Material.QUARTZ_BLOCK,
                        RuntimeKeys.page_hub
                ).build(),
                RuntimeKeys.page_hub
        );
        RuntimePages.MAIN.addItem(RuntimeKeys.page_hub);

        PylonItem.register(NumberStack.class, ItemStackBuilder.pylonItem(
                Material.ENDER_EYE,
                RuntimeKeys.number_stack
        ).build());
        RuntimePages.MAIN.addItem(RuntimeKeys.number_stack);

        PylonItem.register(StringStack.class, ItemStackBuilder.pylonItem(
                Material.HONEYCOMB,
                RuntimeKeys.string_stack
        ).build());
        RuntimePages.MAIN.addItem(RuntimeKeys.string_stack);

        PylonItem.register(FluidTemperatureHolder.class, ItemStackBuilder.pylonItem(
                Material.DRAGON_BREATH,
                RuntimeKeys.fluid_temperature_holder
        ).build());
        RuntimePages.MAIN.addItem(RuntimeKeys.fluid_temperature_holder);

        PylonBlock.register(RuntimeKeys.item_hub, Material.PURPUR_PILLAR, ItemHub.class);
        PylonBlock.register(RuntimeKeys.fluid_hub, Material.LAPIS_BLOCK, FluidHub.class);
        PylonBlock.register(RuntimeKeys.page_hub, Material.QUARTZ_BLOCK, PageHub.class);
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
}

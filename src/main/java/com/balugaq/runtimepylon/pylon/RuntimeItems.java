package com.balugaq.runtimepylon.pylon;

import com.balugaq.runtimepylon.pylon.item.NumberStack;
import com.balugaq.runtimepylon.pylon.item.StringStack;
import com.balugaq.runtimepylon.pylon.item.fluid.FluidTemperatureHolder;
import com.balugaq.runtimepylon.pylon.page.MainPage;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RuntimeItems {
    // @formatter:off
    public static final ItemStack item_hub = ItemStackBuilder.pylon(
            Material.PURPUR_PILLAR,
            RuntimeKeys.item_hub
    ).build();
    public static final ItemStack fluid_hub = ItemStackBuilder.pylon(
            Material.LAPIS_BLOCK,
            RuntimeKeys.fluid_hub
    ).build();
    public static final ItemStack page_hub = ItemStackBuilder.pylon(
            Material.QUARTZ_BLOCK,
            RuntimeKeys.page_hub
    ).build();
    public static final ItemStack number_stack = ItemStackBuilder.pylon(
            Material.ENDER_EYE,
            RuntimeKeys.number_stack
    ).build();
    public static final ItemStack string_stack = ItemStackBuilder.pylon(
            Material.HONEYCOMB,
            RuntimeKeys.string_stack
    ).build();
    public static final ItemStack fluid_temperature_holder = ItemStackBuilder.pylon(
            Material.DRAGON_BREATH,
            RuntimeKeys.fluid_temperature_holder
    ).build();

    static {
        PylonItem.register(
                PylonItem.class,
                item_hub,
                RuntimeKeys.item_hub // block signature
        );
        MainPage.addItem(item_hub);
    }

    static {
        PylonItem.register(
                PylonItem.class,
                fluid_hub,
                RuntimeKeys.fluid_hub
        );
        MainPage.addItem(fluid_hub);
    }

    static {
        PylonItem.register(
                PylonItem.class,
                page_hub,
                RuntimeKeys.page_hub
        );
        MainPage.addItem(page_hub);
    }

    static {
        PylonItem.register(NumberStack.class, number_stack);
        MainPage.addItem(number_stack);
    }

    static {
        PylonItem.register(StringStack.class, string_stack);
        MainPage.addItem(string_stack);
    }

    static {
        PylonItem.register(FluidTemperatureHolder.class, fluid_temperature_holder);
        MainPage.addItem(fluid_temperature_holder);
    }

    public static void initialize() {
    }
}

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
    public static void initialize() {
    }

    // @formatter:off
    public static final ItemStack item_hub = ItemStackBuilder.pylonItem(
            Material.PURPUR_PILLAR,
            RuntimeKeys.item_hub
    ).build();
    static {
        PylonItem.register(
                PylonItem.class,
                item_hub,
                RuntimeKeys.item_hub // block signature
        );
        MainPage.addItem(RuntimeKeys.item_hub);
    }

    public static final ItemStack fluid_hub = ItemStackBuilder.pylonItem(
            Material.LAPIS_BLOCK,
            RuntimeKeys.fluid_hub
    ).build();
    static {
        PylonItem.register(
                PylonItem.class,
                fluid_hub,
                RuntimeKeys.fluid_hub
        );
        MainPage.addItem(RuntimeKeys.fluid_hub);
    }

    public static final ItemStack page_hub = ItemStackBuilder.pylonItem(
            Material.QUARTZ_BLOCK,
            RuntimeKeys.page_hub
    ).build();
    static {
        PylonItem.register(
                PylonItem.class,
                page_hub,
                RuntimeKeys.page_hub
        );
        MainPage.addItem(RuntimeKeys.page_hub);
    }

    public static final ItemStack number_stack = ItemStackBuilder.pylonItem(
            Material.ENDER_EYE,
            RuntimeKeys.number_stack
    ).build();
    static {
        PylonItem.register(NumberStack.class, number_stack);
        MainPage.addItem(RuntimeKeys.number_stack);
    }

    public static final ItemStack string_stack = ItemStackBuilder.pylonItem(
            Material.HONEYCOMB,
            RuntimeKeys.string_stack
    ).build();
    static {
        PylonItem.register(StringStack.class, string_stack);
        MainPage.addItem(RuntimeKeys.string_stack);
    }

    public static final ItemStack fluid_temperature_holder = ItemStackBuilder.pylonItem(
            Material.DRAGON_BREATH,
            RuntimeKeys.fluid_temperature_holder
    ).build();
    static {
        PylonItem.register(FluidTemperatureHolder.class, fluid_temperature_holder);
        MainPage.addItem(RuntimeKeys.fluid_temperature_holder);
    }
}

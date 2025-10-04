package com.balugaq.runtimepylon.pylon.item.fluid;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.balugaq.runtimepylon.gui.ButtonSet.done;
import static com.balugaq.runtimepylon.util.Lang.set_temperature_1;

public class FluidTemperatureHolder extends PylonItem implements PylonFluidTagHolder<FluidTemperature> {
    @Setter
    public @NotNull FluidTemperature temperature = FluidTemperature.NORMAL;

    public FluidTemperatureHolder(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public <K extends PylonBlock & PylonGuiBlock> void onClick(@NotNull K block, @NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event, @NotNull Runnable callback) {
        if (clickType.isLeftClick()) {
            if (clickType.isShiftClick()) {
                switch (temperature) {
                    case COLD -> temperature = FluidTemperature.HOT;
                    case NORMAL -> temperature = FluidTemperature.COLD;
                    case HOT -> temperature = FluidTemperature.NORMAL;
                }
            } else {
                switch (temperature) {
                    case COLD -> temperature = FluidTemperature.NORMAL;
                    case NORMAL -> temperature = FluidTemperature.HOT;
                    case HOT -> temperature = FluidTemperature.COLD;
                }
            }
            getStack().setData(DataComponentTypes.LORE, ItemLore.lore(List.of(temperature.getDisplayText())));
            done(player, set_temperature_1, temperature.getDisplayText());
        }

        callback.run();
    }

    @Override
    public @NotNull FluidTemperature getTag() {
        return temperature;
    }
}

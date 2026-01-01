package com.balugaq.runtimepylon.object;

import com.balugaq.runtimepylon.util.Debug;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.pages.fluid.FluidRecipesPage;
import io.github.pylonmc.pylon.core.guide.pages.fluid.FluidUsagesPage;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jspecify.annotations.NullMarked;

/**
 * {@link Scriptable} proxy methods:
 * - onPreClick
 * - onPreLeftClick
 * - onPreOtherClick
 * - onPostClick
 * - onPostLeftClick
 * - onPostOtherClick
 * 
 * @author balugaq
 */
@NullMarked
public class CustomFluidButton extends FluidButton implements Scriptable {
    @Getter
    private final NamespacedKey key;
    public CustomFluidButton(NamespacedKey key, PylonFluid fluid) {
        super(fluid);
        this.key = key;
    }

    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        try {
            var v2 = callScriptA("onPreClick", this, clickType, player, event);
            if (v2 instanceof Boolean cancelled && cancelled) return;
            if (clickType.isLeftClick()) {
                var v = callScriptA("onPreLeftClick", this, clickType, player, event);
                if (v instanceof Boolean cancelled && cancelled) return;
                FluidRecipesPage page = new FluidRecipesPage(getCurrentFluid().getKey());
                if (!page.getPages().isEmpty()) {
                    page.open(player);
                }
                callScriptA("onPostLeftClick", this, clickType, player, event);
            } else {
                var v = callScriptA("onPreOtherClick", this, clickType, player, event);
                if (v instanceof Boolean cancelled && cancelled) return;
                FluidUsagesPage page = new FluidUsagesPage(getCurrentFluid());
                if (!page.getPages().isEmpty()) {
                    page.open(player);
                }
                callScriptA("onPostOtherClick", this, clickType, player, event);
            }
            callScriptA("onPostClick", this, clickType, player, event);
        } catch (Exception e) {
            Debug.trace(e);
        }
    }
}
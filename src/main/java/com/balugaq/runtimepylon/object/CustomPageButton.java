package com.balugaq.runtimepylon.object;

import com.balugaq.runtimepylon.util.Debug;
import io.github.pylonmc.pylon.core.guide.button.PageButton;
import io.github.pylonmc.pylon.core.guide.pages.base.GuidePage;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jspecify.annotations.NullMarked;

/**
 * {@link Scriptable} proxy methods:
 * - onPreClick
 * - onPostClick
 * 
 * @author balugaq
 */
@NullMarked
public class CustomPageButton extends PageButton implements Scriptable {
    @Getter
    private final NamespacedKey key;
    public CustomPageButton(final NamespacedKey key, final GuidePage page) {
        super(page);
        this.key = key;
    }

    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        try {
            var v2 = callScriptA("onPreClick", this, clickType, player, event);
            if (v2 instanceof Boolean cancelled && cancelled) return;
            getPage().open(player);
            callScriptA("onPostClick", this, clickType, player, event);
        } catch (Exception e) {
            Debug.trace(e);
        }
    }
}

package com.balugaq.pc.object;

import com.balugaq.pc.util.Debug;
import io.github.pylonmc.pylon.core.guide.button.ResearchButton;
import io.github.pylonmc.pylon.core.guide.pages.research.ResearchItemsPage;
import io.github.pylonmc.pylon.core.item.research.Research;
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
 * - onPreRightClick
 * - onPreMiddleClick
 * - onPostClick
 * - onPostLeftClick
 * - onPostRightClick
 * - onPostMiddleClick
 * - onOtherClick
 * 
 * @author balugaq
 */
@Getter
@NullMarked
public class CustomResearchButton extends ResearchButton implements Scriptable {
    private final NamespacedKey key;

    public CustomResearchButton(NamespacedKey key, Research research) {
        super(research);
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
                if (getResearch().isResearchedBy(player) || getResearch().cost() == null || getResearch().cost() > Research.getResearchPoints(player)) {
                    return;
                }
                getResearch().addTo(player);
                Research.setResearchPoints(player, Research.getResearchPoints(player) - getResearch().cost());
                this.getWindows().forEach(window -> {
                    window.close();
                    window.open();
                });
                callScriptA("onPostLeftClick", this, clickType, player, event);
            } else if (clickType.isRightClick()) {
                var v = callScriptA("onPreRightClick", this, clickType, player, event);
                if (v instanceof Boolean cancelled && cancelled) return;
                new ResearchItemsPage(getResearch()).open(player);
                callScriptA("onPostRightClick", this, clickType, player, event);
            } else if (clickType == ClickType.MIDDLE) {
                var v = callScriptA("onPreMiddleClick", this, clickType, player, event);
                if (v instanceof Boolean cancelled && cancelled) return;
                if (player.hasPermission("pylon.command.research.modify")) {
                    getResearch().addTo(player);
                    this.notifyWindows();
                }
                callScriptA("onPostMiddleClick", this, clickType, player, event);
            } else {
                callScriptA("onOtherClick", this, clickType, player, event);
            }
        } catch (Exception e) {
            Debug.trace(e);
        }
        callScriptA("onPostClick", this, clickType, player, event);
    }
}
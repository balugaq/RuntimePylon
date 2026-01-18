package com.balugaq.pc.object;

import com.balugaq.pc.util.Debug;
import io.github.pylonmc.pylon.core.guide.button.PageButton;
import io.github.pylonmc.pylon.core.guide.pages.base.GuidePage;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
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
    public CustomPageButton(final NamespacedKey key, final ItemStack stack, final GuidePage page) {
        super(stack, page);
        this.key = key;
    }

    public CustomPageButton(final NamespacedKey key, final Material material, final GuidePage page) {
        this(key, ItemStack.of(material), page);
    }

    public CustomPageButton(final ItemStack stack, final SimpleStaticGuidePage page) {
        this(page.getKey(), stack, page);
    }

    public CustomPageButton(final Material material, final SimpleStaticGuidePage page) {
        this(page.getKey(), material, page);
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

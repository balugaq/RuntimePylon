package com.balugaq.runtimepylon;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import static com.balugaq.runtimepylon.GuiItem.*;
import java.util.Map;

public class Buttons <T extends PylonBlock & PylonGuiBlock> {
    public static <T extends PylonBlock & PylonGuiBlock> ClickHandler<T> deny() {
        return (data, clickType, player, event) -> {
            event.setCancelled(true);
        };
    }

    public static <T extends PylonBlock & PylonGuiBlock> ClickHandler<T> allow() {
        return (data, clickType, player, event) -> {};
    }

    public T block;
    public Buttons(@NotNull T block) {
        this.block = block;
    }

    public GuiItem<T> create() {
        return GuiItem.create(block);
    }

    public final AbstractItem

    blackBackground = create()
            .item(ItemStackBuilder.pylonItem(
                    Material.BLACK_STAINED_GLASS_PANE,
                    Key.create("black_background")
            ))
            .click(deny()),

    grayBackground = create()
            .item(ItemStackBuilder.pylonItem(
                    Material.GRAY_STAINED_GLASS_PANE,
                    Key.create("gray_background")
            ))
            .click(deny()),
    
    setItemGroup = create()
            .item()
            .click((block, clickType, player, event) -> {
                var data = assertBlock(block, ItemHub.class);

                assertNotNull(data.itemId, "Not set item id");
                assertNotNull(data.groupId, "Not set group id");

                Map<NamespacedKey, SimpleStaticGuidePage> pages = RuntimePylon.getGuidePages();
                SimpleStaticGuidePage page = assertNotNull(pages.get(data.groupId), "Unknown group");

                assertNotNull(PylonRegistry.ITEMS.get(data.itemId), "Unknown item");
                page.addItem(data.itemId);

                done(player, "Added {} to {}", data.itemId, data.groupId);
            }),

    setRecipe = create()
            .item()
            .click((block, clickType, player, event) -> {
                var data = assertBlock(block, WithRecipe.class);

            });
}

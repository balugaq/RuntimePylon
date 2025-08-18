package com.balugaq.runtimepylon;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.impl.AbstractItem;

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
                if (!(block instanceof ItemGenerator data)) return;

                data.itemId;
            });
}

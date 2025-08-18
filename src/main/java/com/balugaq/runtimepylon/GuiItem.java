package com.balugaq.runtimepylon;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

@Getter
public class GuiItem<T extends PylonBlock & PylonGuiBlock> extends AbstractItem {
    private ItemProvider itemProvider;
    private ClickHandler<T> clickHandler;
    private final T data;

    public GuiItem(@NotNull T data) {
        this.data = data;
        this.itemProvider = ItemProvider.EMPTY;
        this.clickHandler = Buttons.deny();
    }

    public GuiItem(@NotNull T data, @NotNull ItemProvider itemProvider, @NotNull ClickHandler<T> clickHandler) {
        this.data = data;
        this.itemProvider = itemProvider;
        this.clickHandler = clickHandler;
    }

    public static <T extends PylonBlock & PylonGuiBlock> GuiItem<T> create(@NotNull T data, @NotNull ItemProvider itemProvider, @NotNull ClickHandler<T> clickHandler) {
        return new GuiItem<>(data, itemProvider, clickHandler);
    }

    public static <T extends PylonBlock & PylonGuiBlock> GuiItem<T> create(@NotNull T  data) {
        return new GuiItem<>(data);
    }

    public GuiItem<T> item(@NotNull ItemProvider itemProvider) {
        this.itemProvider = itemProvider;
        return this;
    }

    public GuiItem<T> click(@NotNull Class<T> clazz, @NotNull ClickHandler<T> clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    public GuiItem<T> click(@NotNull ClickHandler<T> clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemProvider;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        clickHandler.handleClick(data, clickType, player, event);
    }
}

package com.balugaq.runtimepylon;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.helpers.MessageFormatter;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class GuiItem<T extends PylonBlock & PylonGuiBlock> extends AbstractItem {
    private Function<T, ItemProvider> itemProvider;
    private ClickHandler<T> clickHandler;
    private final T data;

    public GuiItem(@NotNull T data) {
        this.data = data;
        this.itemProvider = block -> null;
        this.clickHandler = ButtonSet.deny();
    }

    public static <T extends PylonBlock & PylonGuiBlock> GuiItem<T> create(@NotNull T  data) {
        return new GuiItem<>(data);
    }

    public GuiItem<T> item(@NotNull Function<T, ItemProvider> itemProvider) {
        this.itemProvider = itemProvider;
        return this;
    }

    public GuiItem<T> click(@NotNull ClickHandler<T> clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemProvider.apply(data);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        try {
            clickHandler.handleClick(data, clickType, player, event);
        } catch (WrongStateException e) {
            player.sendMessage(e.getMessage());
            return;
        }
    }

    public static <T> T assertNotNull(@Nullable T o, @NotNull String message) {
        if (o == null) throw new WrongStateException(message);
        return o;
    }

    public static void assertTrue(boolean stmt, @NotNull String message) {
        if (!stmt) throw new WrongStateException(message);
    }

    public static void assertFalse(boolean stmt, @NotNull String message) {
        if (stmt) throw new WrongStateException(message);
    }

    public static void done(@NotNull Player player, @NotNull String literal, @NotNull Object... args) {
        player.sendMessage(MessageFormatter.arrayFormat(literal, args).getMessage());
    }

    public static void done(@NotNull Player player, @NotNull ComponentLike component) {
        player.sendMessage(component.asComponent());
    }

    public static <T extends PylonBlock & PylonGuiBlock, K> K assertBlock(@NotNull T block, @NotNull Class<K> expected) {
        if (expected.isInstance(block)) {
            return expected.cast(block);
        } else {
            throw new WrongStateException("Not " + expected.getSimpleName());
        }
    }

    @Nullable
    public static NamespacedKey toNamespacedKey(@NotNull String string) {
        return NamespacedKey.fromString(string);
    }

    public static void waitInput(@NotNull Player player, @NotNull String literal, @NotNull Consumer<String> consumer) {
        waitInput(player, Component.text(literal), consumer);
    }

    public static void waitInput(@NotNull Player player, @NotNull ComponentLike component, @NotNull Consumer<String> consumer) {

    }
}

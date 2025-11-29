package com.balugaq.runtimepylon.gui;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.exceptions.IgnorableException;
import com.balugaq.runtimepylon.exceptions.WrongStateException;
import com.balugaq.runtimepylon.listener.ChatInputListener;
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
import org.jspecify.annotations.NullMarked;
import org.slf4j.helpers.MessageFormatter;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.balugaq.runtimepylon.util.Lang.gui_err_1;

/**
 * @author balugaq
 */
@Getter
@NullMarked
public class GuiItem<T extends PylonBlock & PylonGuiBlock> extends AbstractItem {
    private final T data;
    private Function<T, @Nullable ItemProvider> itemProvider;
    private ClickHandler<T> clickHandler;

    public GuiItem(T data) {
        this.data = data;
        this.itemProvider = block -> null;
        this.clickHandler = ButtonSet.deny();
    }

    public static <T extends PylonBlock & PylonGuiBlock> GuiItem<T> create(@NotNull T data) {
        return new GuiItem<>(data);
    }

    public static <T> T assertNotNull(@Nullable T o) {
        if (o == null) throw new IgnorableException();
        return o;
    }

    public static <T> T assertNotNull(@Nullable T o, String message) {
        if (o == null) throw new WrongStateException(message);
        return o;
    }

    public static void assertFalse(boolean stmt, String message) {
        assertTrue(!stmt, message);
    }

    public static void assertTrue(boolean stmt, String message) {
        if (!stmt) throw new WrongStateException(message);
    }

    public static void done(Player player, String literal, Object... args) {
        player.sendMessage(MessageFormatter.arrayFormat(literal, args).getMessage());
    }

    public static void done(Player player, ComponentLike component) {
        player.sendMessage(component.asComponent());
    }

    public static <T extends PylonBlock & PylonGuiBlock, K> K assertBlock(T block, Class<K> expected) {
        if (expected.isInstance(block)) {
            return expected.cast(block);
        } else {
            throw new WrongStateException(gui_err_1 + expected.getSimpleName());
        }
    }

    @Nullable
    public static NamespacedKey toNamespacedKey(String string) {
        return NamespacedKey.fromString(string, RuntimePylon.getInstance());
    }

    public static void waitInput(Player player, String literal, Consumer<String> callback) {
        waitInput(player, Component.text(literal), callback);
    }

    public static void waitInput(Player player, ComponentLike component, Consumer<String> callback) {
        player.sendMessage(component);
        player.closeInventory();
        ChatInputListener.waitInput(player.getUniqueId(), callback);
    }

    public GuiItem<T> item(Function<T, ItemProvider> itemProvider) {
        this.itemProvider = itemProvider;
        return this;
    }

    public GuiItem<T> click(ClickHandler<T> clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    @Override
    public @Nullable ItemProvider getItemProvider() {
        return itemProvider.apply(data);
    }

    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        try {
            boolean updateWindow = clickHandler.handleClick(data, clickType, player, event);
            if (updateWindow) notifyWindows();
        } catch (Exception e) {
            if (e instanceof IgnorableException) {
                return;
            }
            if (e instanceof WrongStateException) {
                player.sendMessage(e.getMessage());
                return;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}

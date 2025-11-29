package com.balugaq.runtimepylon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author balugaq
 */
public class ChatInputListener implements Listener {
    private static final Map<UUID, Consumer<String>> callbacks = new HashMap<>();

    public static void waitInput(@NotNull UUID uuid, @NotNull Consumer<String> callback) {
        callbacks.put(uuid, callback);
    }

    @EventHandler
    public void onChatInput(@NotNull AsyncPlayerChatEvent event) {
        Optional.ofNullable(callbacks.get(event.getPlayer().getUniqueId())).ifPresent(callback -> {
            event.setCancelled(true);
            callback.accept(event.getMessage());
        });
    }
}

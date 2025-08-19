package com.balugaq.runtimepylon.input;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputListener implements Listener {
    private static final Map<UUID, Consumer<String>> callbacks = new HashMap<>();

    @EventHandler
    public void onChatInput(@NotNull AsyncPlayerChatEvent event) {
        Optional.ofNullable(callbacks.get(event.getPlayer().getUniqueId())).ifPresent(callback -> {
            callback.accept(event.getMessage());
        });
    }

    public static void waitInput(@NotNull UUID uuid, @NotNull Consumer<String> callback) {
        callbacks.put(uuid, callback);
    }
}

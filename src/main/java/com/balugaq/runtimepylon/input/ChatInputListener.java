package com.balugaq.runtimepylon.input;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputListener implements Listener {
    private static final Map<UUID, Consumer<Component>> callbacks = new HashMap<>();

    @EventHandler
    public void onChatInput(@NotNull AsyncChatEvent event) {
        Optional.ofNullable(callbacks.get(event.getPlayer().getUniqueId())).ifPresent(callback -> {
            callback.accept(event.message());
        });
    }

    public static void waitInput(@NotNull UUID uuid, @NotNull Consumer<Component> callback) {
        callbacks.put(uuid, callback);
    }
}

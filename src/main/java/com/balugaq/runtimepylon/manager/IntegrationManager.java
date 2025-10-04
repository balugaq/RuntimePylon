package com.balugaq.runtimepylon.manager;

import com.balugaq.runtimepylon.RuntimePylon;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class IntegrationManager {
    public @NotNull IsEnabled isEnabled;

    public IntegrationManager() {
        this.isEnabled = new IsEnabled();
        onServerDone(() -> {
            this.isEnabled.pylonBase = Bukkit.getPluginManager().getPlugin("PylonBase") != null;
        });
    }

    public static IntegrationManager instance() {
        return RuntimePylon.getInstance().getIntegrationManager();
    }

    public void onServerDone(@NotNull Runnable runnable) {
        RuntimePylon.runTaskLater(runnable, 1);
    }

    public static class IsEnabled {
        public boolean pylonBase;
    }
}

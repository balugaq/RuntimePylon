package com.balugaq.runtimepylon;

import org.bukkit.Bukkit;

public class IntegrationManager {
    public IsEnabled isEnabled;

    public IntegrationManager() {
        this.isEnabled = new IsEnabled();
        onServerDone(() -> {
            this.isEnabled.pylonBase = Bukkit.getPluginManager().getPlugin("PylonBase") != null;
        });
    }

    public void onServerDone(Runnable runnable) {
        RuntimePylon.runTaskLater(runnable, 1);
    }

    public static class IsEnabled {
        public boolean pylonBase;
    }
}

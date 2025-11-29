package com.balugaq.runtimepylon.manager;

import com.balugaq.runtimepylon.RuntimePylon;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
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

    public static IntegrationManager instance() {
        return RuntimePylon.getIntegrationManager();
    }

    /**
     * @author  balugaq
     */
    @NullMarked
    public static class IsEnabled {
        public boolean pylonBase;
    }
}

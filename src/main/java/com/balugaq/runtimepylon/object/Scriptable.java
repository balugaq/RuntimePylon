package com.balugaq.runtimepylon.object;

import com.balugaq.runtimepylon.GlobalVars;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public interface Scriptable {
    @CanIgnoreReturnValue
    @Nullable
    default Object callScript(Object... objects) {
        var script = GlobalVars.getScriptO(getKey());
        if (script.isPresent()) {
            return script.get().executeFunction(
                    getCallerMethodName(),
                    objects
            ).get();
        }
        return null;
    }

    NamespacedKey getKey();

    @Internal
    private static String getCallerMethodName() {
        return StackWalker.getInstance()
                .walk(s -> s.skip(3).findFirst())
                .get()
                .getMethodName();
    }

    @CanIgnoreReturnValue
    @Nullable
    default <T extends Event & Cancellable> Object callOrCancelEvent(Scriptable self, T event, Object... objects) {
        String caller = getCallerMethodName();
        var v = callScriptA(caller, self, event, objects);
        if (v == null) event.setCancelled(true);
        return v;
    }

    @CanIgnoreReturnValue
    @Nullable
    default Object callScriptA(String methodName, Object... objects) {
        return GlobalVars.getScriptO(getKey()).ifPresent(script -> script.executeFunction(
                methodName,
                objects
        )).orElse(null);
    }

    default boolean isFunctionExists(String functionName) {
        var o = GlobalVars.getScriptO(getKey());
        if (o.isEmpty()) return false;
        return o.get().isFunctionExists(functionName);
    }
}

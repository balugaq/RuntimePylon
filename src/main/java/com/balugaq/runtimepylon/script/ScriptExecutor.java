package com.balugaq.runtimepylon.script;

import com.balugaq.runtimepylon.GlobalVars;
import com.balugaq.runtimepylon.util.Debug;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract script executor
 *
 * @author lijinhong11
 */
@NullMarked
public abstract class ScriptExecutor {
    private final Set<String> failedFunctions = new HashSet<>();

    @Getter
    private final File file;

    public ScriptExecutor(File file) {
        this.file = file;
    }

    @CanIgnoreReturnValue
    public final GlobalVars.Result<?> executeFunction(String functionName, Object... parameters) {
        if (failedFunctions.contains(functionName)) {
            return GlobalVars.Result.EMPTY;
        }

        if (!isFunctionExists(functionName)) {
            return GlobalVars.Result.EMPTY;
        }

        try {
            return GlobalVars.Result.of(executeFunction0(functionName, parameters));
        } catch (Exception e) {
            Debug.severe(e);
            failedFunctions.add(functionName);
            return GlobalVars.Result.EMPTY;
        }
    }

    public abstract boolean isFunctionExists(String functionName);

    protected abstract @Nullable Object executeFunction0(String functionName, Object... parameters) throws Exception;

    public abstract void close();
}

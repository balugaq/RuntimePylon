package com.balugaq.runtimepylon.script;

import lombok.Getter;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract script executor
 *
 * @author lijinhong11
 */
public abstract class ScriptExecutor {
    private final Set<String> failedFunctions = new HashSet<>();

    @Getter
    private final File file;

    public ScriptExecutor(File file) {
        this.file = file;
    }

    public final Object executeFunction(String functionName, Object... parameters) {
        if (failedFunctions.contains(functionName)) {
            return null;
        }

        if (!isFunctionExists(functionName)) {
            return null;
        }

        try {
            return executeFunction0(functionName, parameters);
        } catch (Exception e) {
            failedFunctions.add(functionName);
            return null;
        }
    }

    public abstract void close();

    public abstract boolean isFunctionExists(String functionName);

    protected abstract Object executeFunction0(String functionName, Object... parameters) throws Exception;
}

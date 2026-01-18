package com.balugaq.pc.script;

import com.balugaq.pc.GlobalVars;
import com.balugaq.pc.util.Debug;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8Script;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.File;

/**
 * @author lijinhong11
 */
@NullMarked
public class JSScriptExecutor extends ScriptExecutor {
    private final ObjectOpenHashSet<String> failedFunctions = new ObjectOpenHashSet<>();
    @Nullable private V8ValueObject scriptObject;

    public JSScriptExecutor(File file) {
        super(file);
        load();
    }

    private void load() {
        try {
            try (V8Script script = GlobalVars.getScriptRuntime().getExecutor(getFile()).compileV8Script()) {
                var v = script.execute();
                if (v instanceof V8ValueObject vo) {
                    scriptObject = vo;
                } else {
                    Debug.severe("Failed to create JSScriptExecutor: script.execute()=" + v);
                }
            }
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFunctionExists(String functionName) {
        if (failedFunctions.contains(functionName)) return false;
        try {
            V8Value value = GlobalVars.getScriptRuntime().getGlobalObject().get(functionName);
            boolean exists = value instanceof V8ValueFunction;
            if (value != null) {
                value.close();
            }
            return exists;
        } catch (JavetException e) {
            failedFunctions.add(functionName);
            return false;
        }
    }

    @Override
    @Nullable
    protected Object executeFunction0(String functionName, @Nullable Object... parameters) throws Exception {
        if (scriptObject == null) return null;
        try (V8Value value = scriptObject.get(functionName)) {
            if (!(value instanceof V8ValueFunction function)) {
                throw new NoSuchMethodException("Function " + functionName + " not found in script");
            }

            try (V8Value result = function.call(scriptObject, parameters)) {
                return result == null ? null : GlobalVars.getScriptRuntime().toObject(result);
            }
        }
    }

    public void close() {
        try {
            if (scriptObject != null) scriptObject.close();
        } catch (Exception ignored) {
        }
    }
}

package com.balugaq.runtimepylon.script;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.script.callbacks.APICallbacks;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8Script;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.File;

/**
 * @author lijinhong11
 */
@NullMarked
public class JSScriptExecutor extends ScriptExecutor {
    private V8ValueObject scriptObject;

    public JSScriptExecutor(File file) {
        super(file);
        load();
    }

    private void load() {
        try {
            try (V8Script script = RuntimePylon.getInstance().getScriptRuntime().getExecutor(getFile()).compileV8Script()) {
                scriptObject = script.execute();
            }
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFunctionExists(String functionName) {
        try {
            V8Value value = RuntimePylon.getInstance().getScriptRuntime().getGlobalObject().get(functionName);
            boolean exists = value instanceof V8ValueFunction;
            if (value != null) {
                value.close();
            }
            return exists;
        } catch (JavetException e) {
            return false;
        }
    }

    @Override
    @Nullable
    protected Object executeFunction0(String functionName, Object... parameters) throws Exception {
        try (V8Value value = scriptObject.get(functionName)) {
            if (!(value instanceof V8ValueFunction function)) {
                throw new NoSuchMethodException("Function " + functionName + " not found in script");
            }

            try (V8Value result = function.call(scriptObject, parameters)) {
                return result == null ? null : RuntimePylon.getInstance().getScriptRuntime().toObject(result);
            }
        }
    }

    public void close() {
        try {
            scriptObject.close();
        } catch (Exception ignored) {
        }
    }
}

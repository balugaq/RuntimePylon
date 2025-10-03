package com.balugaq.runtimepylon.script;

import com.balugaq.runtimepylon.script.callbacks.PylonCallbackReceiver;
import com.balugaq.runtimepylon.script.callbacks.RuntimePylonCallbackReceiver;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8Script;
import com.caoccao.javet.values.reference.V8ValueFunction;

import java.io.File;

/**
 * @author lijinhong11
 */
public class JSScriptExecutor extends ScriptExecutor {
    private final V8Runtime runtime;
    private V8Script script;

    public JSScriptExecutor(File file) {
        super(file);
        try {
            runtime = V8Host.getV8Instance().createV8Runtime();
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
        load();
    }

    private void load() {
        try {
            runtime.getGlobalObject().bind(new PylonCallbackReceiver());
            runtime.getGlobalObject().bind(new RuntimePylonCallbackReceiver());

            script = runtime.getExecutor(getFile()).compileV8Script();
            script.executeVoid();
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            if (script != null) {
                script.close();
            }
            runtime.close();
        } catch (JavetException ignored) {
        }
    }

    public boolean isFunctionExists(String functionName) {
        try {
            V8Value value = runtime.getGlobalObject().get(functionName);
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
    protected Object executeFunction0(String functionName, Object... parameters) throws Exception {
        try (V8Value value = runtime.getGlobalObject().get(functionName)) {
            if (!(value instanceof V8ValueFunction function)) {
                throw new NoSuchMethodException("Function " + functionName + " not found in script");
            }

            try (V8Value result = function.call(runtime.getGlobalObject(), parameters)) {
                return result == null ? null : runtime.toObject(result);
            }
        }
    }
}

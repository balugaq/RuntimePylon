package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.ScriptDesc;
import com.balugaq.runtimepylon.script.JSScriptExecutor;
import com.balugaq.runtimepylon.script.ScriptExecutor;
import com.balugaq.runtimepylon.util.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A collection for pack scripts
 *
 * @author lijinhong11
 */
@Data
@NoArgsConstructor(force = true)
@NullMarked
public class Scripts implements FileObject<Scripts> {
    private final Map<String, ScriptExecutor> scripts = new HashMap<>();

    @Nullable
    public ScriptExecutor findScript(ScriptDesc scriptDesc) {
        return scripts.get(scriptDesc.getScriptPath());
    }

    public void closeAll() {
        // todo
    }

    @Override
    public List<FileReader<Scripts>> readers() {
        return List.of(
                dir -> {
                    for (File file : dir.listFiles()) {
                        if (!file.isFile()) continue;
                        if (!file.getName().endsWith(".js")) continue;
                        scripts.put(StringUtil.simplifyPath(file.getPath()), createJSScriptExecutor(file));
                    }

                    return this;
                }
        );
    }

    private ScriptExecutor createJSScriptExecutor(File file) {
        return new JSScriptExecutor(file);
    }
}

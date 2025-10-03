package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.ExamineFailedException;
import com.balugaq.runtimepylon.script.ScriptExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class ScriptDesc implements Deserializable<ScriptDesc>, Examinable<ScriptDesc> {
    private final String scriptName; // e. g. myscript.js

    @Override
    public ScriptDesc examine() throws ExamineFailedException {
        if (!scriptName.matches("[A-Za-z0-9_+-]+")) {
            throw new ExamineFailedException("Pack Desc must be [A-Za-z0-9_+-]+");
        }
        return this;
    }

    @Override
    public List<ConfigReader<?, ScriptDesc>> readers() {
        return List.of(
                ConfigReader.of(String.class, ScriptDesc::new)
        );
    }
}

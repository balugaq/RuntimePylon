package com.balugaq.pc.config;

import com.balugaq.pc.exceptions.ExamineFailedException;
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
public class ScriptDesc implements Deserializer<ScriptDesc>, Examinable<ScriptDesc> {
    private final String scriptPath; // e. g. myscript.js

    @Override
    public ScriptDesc examine() throws ExamineFailedException {
        if (!scriptPath.matches("[A-Za-z0-9_+\\-/]+")) {
            throw new ExamineFailedException("Script Desc must be [A-Za-z0-9_+-]+");
        }
        return this;
    }

    @Override
    public List<ConfigReader<?, ScriptDesc>> readers() {
        return ConfigReader.list(String.class, ScriptDesc::new);
    }
}

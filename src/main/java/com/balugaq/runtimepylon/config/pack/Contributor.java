package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Examinable;
import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializable;
import com.balugaq.runtimepylon.exceptions.ExamineFailedException;
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
public class Contributor implements Deserializable<Contributor>, Examinable<Contributor> {
    private final String name;

    @Override
    public Contributor examine() throws ExamineFailedException {
        if (!name.matches(".+")) {
            throw new ExamineFailedException("Contributor must be .+");
        }
        return this;
    }

    @Override
    public List<ConfigReader<?, Contributor>> readers() {
        return List.of(
                ConfigReader.of(String.class, Contributor::new)
        );
    }
}

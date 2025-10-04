package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.Examinable;
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
public class Author implements Deserializer<Author>, Examinable<Author> {
    private final String name;

    @Override
    public Author examine() throws ExamineFailedException {
        if (!name.matches(".+")) {
            throw new ExamineFailedException("Author must be .+");
        }
        return this;
    }

    @Override
    public List<ConfigReader<?, Author>> readers() {
        return List.of(
                ConfigReader.of(String.class, Author::new)
        );
    }
}

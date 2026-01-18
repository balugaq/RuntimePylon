package com.balugaq.pc.config.pack;

import com.balugaq.pc.config.ConfigReader;
import com.balugaq.pc.config.Deserializer;
import com.balugaq.pc.config.Examinable;
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
        return ConfigReader.list(String.class, Author::new);
    }
}

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
public class WebsiteLink implements Deserializer<WebsiteLink>, Examinable<WebsiteLink> {
    private final String link;

    @Override
    public WebsiteLink examine() throws ExamineFailedException {
        if (!link.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$")) {
            throw new ExamineFailedException("WebsiteLink must be ^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
        }
        return this;
    }

    @Override
    public List<ConfigReader<?, WebsiteLink>> readers() {
        return ConfigReader.list(String.class, WebsiteLink::new);
    }
}

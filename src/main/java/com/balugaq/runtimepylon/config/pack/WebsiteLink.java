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
public class WebsiteLink implements Deserializable<WebsiteLink>, Examinable<WebsiteLink> {
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
        return List.of(
                ConfigReader.of(String.class, WebsiteLink::new)
        );
    }
}

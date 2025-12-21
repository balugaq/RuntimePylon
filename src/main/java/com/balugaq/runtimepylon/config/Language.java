package com.balugaq.runtimepylon.config;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Locale;

/**
 * @author balugaq
 */
@NullMarked
public record Language(String localeCode) implements Deserializer<Language> {
    public Language() {
        this("en");
    }

    public Locale locale() {
        String[] codes = this.localeCode.split("_");
        if (codes.length == 1) {
            return Locale.of(codes[0]);
        }
        if (codes.length == 2) {
            return Locale.of(codes[0], codes[1]);
        }
        return Locale.of(codes[0], codes[1], codes[2]);
    }

    @Override
    public List<ConfigReader<?, Language>> readers() {
        return ConfigReader.list(String.class, Language::new);
    }
}

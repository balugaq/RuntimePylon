package com.balugaq.runtimepylon.config;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Locale;

@NullMarked
public record Language(String localeCode) implements Deserializer<Language> {
    public Language() {
        this("en");
    }

    public Locale locale() {
        String[] codes = this.localeCode.split("-");
        if (codes.length == 1) {
            return Locale.of(codes[0]);
        }
        if (codes.length == 2) {
            return Locale.of(codes[0], codes[1]);
        }
        return Locale.of(codes[0], codes[1], codes[2]);
    }

    @Override
    public @NotNull List<ConfigReader<?, Language>> readers() {
        return List.of(ConfigReader.of(String.class, Language::new));
    }
}

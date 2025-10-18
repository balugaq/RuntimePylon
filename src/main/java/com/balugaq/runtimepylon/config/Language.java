package com.balugaq.runtimepylon.config;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record Language(String locale) implements Deserializer<Language> {
    public Language() {
        this("en");
    }

    @Override
    public @NotNull List<ConfigReader<?, Language>> readers() {
        return List.of(ConfigReader.of(String.class, Language::new));
    }
}

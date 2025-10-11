package com.balugaq.runtimepylon.config;

import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public record PostLoadTask<T extends PostLoadable>(T postLoadable, Consumer<T> consumer) {
    public static <T extends PostLoadable> PostLoadTask<T> of(T postLoadable, Consumer<T> consumer) {
        return new PostLoadTask<>(postLoadable, consumer);
    }

    public void run() {
        consumer.accept(postLoadable);
    }
}

package com.balugaq.runtimepylon.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@Data
@AllArgsConstructor
@NullMarked
public class PostLoadTask<T extends PostLoadable> {
    private final T postLoadable;
    private final Consumer<T> consumer;

    public static <T extends PostLoadable> PostLoadTask<T> of(T postLoadable, Consumer<T> consumer) {
        return new PostLoadTask<>(postLoadable, consumer);
    }

    public void run() {
        consumer.accept(postLoadable);
    }
}

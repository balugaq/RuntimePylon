package com.balugaq.pc.config;

import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/**
 * @author balugaq
 */
@NullMarked
public record PostLoadTask<T extends PostLoadable>(T postLoadable, Consumer<T> consumer) {
    public static <T extends PostLoadable> PostLoadTask<T> of(T postLoadable, Consumer<T> consumer) {
        return new PostLoadTask<>(postLoadable, consumer);
    }

    public void run() {
        consumer.accept(postLoadable);
    }
}

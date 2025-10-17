package com.balugaq.runtimepylon.config;

import org.jspecify.annotations.NullMarked;

import java.io.File;

/**
 * @param <Result> The result type
 * @author balugaq
 */
@FunctionalInterface
@NullMarked
public interface FileReader<Result> {
    Result read(File dir);
}

package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.UnrecognizedFileException;
import org.jspecify.annotations.NullMarked;

import java.io.File;

/**
 * @param <Result> The result type
 * @author balugaq
 */
@FunctionalInterface
@NullMarked
public interface FileReader<Result> {
    Result read(File dir) throws UnrecognizedFileException;
}

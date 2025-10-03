package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.UnrecognizedFileException;
import org.jspecify.annotations.NullMarked;

import java.io.File;

/**
 * @author balugaq
 * @param <Result> The result type
 */
@FunctionalInterface
@NullMarked
public interface FileReader<Result> {
    Result read(File dir) throws UnrecognizedFileException;
}

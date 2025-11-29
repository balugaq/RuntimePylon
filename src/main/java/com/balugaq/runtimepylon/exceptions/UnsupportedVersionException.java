package com.balugaq.runtimepylon.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class UnsupportedVersionException extends RuntimeException {
    public UnsupportedVersionException() {
        super();
    }

    public UnsupportedVersionException(String message) {
        super(message);
    }
}

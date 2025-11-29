package com.balugaq.runtimepylon.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class IncompatibleKeyFormatException extends RuntimeException {
    public IncompatibleKeyFormatException() {
        super();
    }

    public IncompatibleKeyFormatException(String message) {
        super(message);
    }
}

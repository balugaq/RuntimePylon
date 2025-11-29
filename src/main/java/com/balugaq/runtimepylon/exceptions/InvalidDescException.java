package com.balugaq.runtimepylon.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class InvalidDescException extends RuntimeException {
    public InvalidDescException() {
        super();
    }

    public InvalidDescException(String message) {
        super(message);
    }
}

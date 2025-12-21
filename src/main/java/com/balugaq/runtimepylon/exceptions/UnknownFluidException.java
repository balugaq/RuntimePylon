package com.balugaq.runtimepylon.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class UnknownFluidException extends RuntimeException {
    public UnknownFluidException() {
        super();
    }

    public UnknownFluidException(String message) {
        super(message);
    }
}

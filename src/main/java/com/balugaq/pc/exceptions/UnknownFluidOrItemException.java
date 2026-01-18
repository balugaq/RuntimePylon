package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class UnknownFluidOrItemException extends RuntimeException {
    public UnknownFluidOrItemException() {
        super();
    }

    public UnknownFluidOrItemException(String message) {
        super(message);
    }
}

package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class UnknownItemException extends RuntimeException {
    public UnknownItemException() {
        super();
    }

    public UnknownItemException(String message) {
        super(message);
    }
}

package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class InvalidMultiblockComponentException extends RuntimeException {
    public InvalidMultiblockComponentException() {
        super();
    }

    public InvalidMultiblockComponentException(String message) {
        super(message);
    }
}

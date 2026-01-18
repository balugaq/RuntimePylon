package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class UnknownMultiblockComponentException extends RuntimeException {
    public UnknownMultiblockComponentException() {
        super();
    }

    public UnknownMultiblockComponentException(String message) {
        super(message);
    }
}

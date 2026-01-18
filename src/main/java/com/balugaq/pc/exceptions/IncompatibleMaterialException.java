package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class IncompatibleMaterialException extends RuntimeException {
    public IncompatibleMaterialException() {
        super();
    }

    public IncompatibleMaterialException(String message) {
        super(message);
    }
}

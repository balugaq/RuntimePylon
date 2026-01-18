package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class UnknownEnumException extends RuntimeException {
    public UnknownEnumException() {
        super();
    }

    public UnknownEnumException(Class<?> e, String id) {
        super("Unknown element: " + e.getSimpleName() + "." + id);
    }
}

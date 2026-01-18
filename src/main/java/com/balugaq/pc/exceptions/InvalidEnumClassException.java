package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class InvalidEnumClassException extends RuntimeException {
    public InvalidEnumClassException() {
        super();
    }

    public InvalidEnumClassException(String clazz) {
        super("Unknown class: " + clazz);
    }
}

package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class InvalidNamespacedKeyException extends RuntimeException {
    public InvalidNamespacedKeyException() {
        super();
    }

    public InvalidNamespacedKeyException(String message) {
        super(message);
    }
}

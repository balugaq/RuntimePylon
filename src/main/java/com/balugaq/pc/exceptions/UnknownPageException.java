package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class UnknownPageException extends RuntimeException {
    public UnknownPageException() {
        super();
    }

    public UnknownPageException(String message) {
        super(message);
    }
}

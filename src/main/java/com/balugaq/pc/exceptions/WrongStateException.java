package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class WrongStateException extends RuntimeException {
    public WrongStateException(String message) {
        super(message);
    }
}

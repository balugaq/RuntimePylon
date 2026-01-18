package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class UnknownSymbolException extends RuntimeException {
    public UnknownSymbolException() {
        super();
    }

    public UnknownSymbolException(String message) {
        super(message);
    }
}

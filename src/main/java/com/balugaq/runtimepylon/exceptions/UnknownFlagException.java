package com.balugaq.runtimepylon.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class UnknownFlagException extends PackException {
    public UnknownFlagException() {
        super();
    }

    public UnknownFlagException(String message) {
        super(message);
    }
}

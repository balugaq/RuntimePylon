package com.balugaq.runtimepylon.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class MissingArgumentException extends PackException {
    public MissingArgumentException() {
        super();
    }

    public MissingArgumentException(String message) {
        super(message);
    }

    public MissingArgumentException(Class<?> clazz) {
        super("Missing argument for " + clazz.getSimpleName());
    }
}

package com.balugaq.runtimepylon.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class MissingFileException extends PackException {
    public MissingFileException() {
        super();
    }

    public MissingFileException(String message) {
        super(message);
    }
}

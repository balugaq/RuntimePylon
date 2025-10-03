package com.balugaq.runtimepylon.exceptions;

/**
 * @author balugaq
 */
public class MissingArgumentException extends RuntimeException {
    public MissingArgumentException() {
        super();
    }
    public MissingArgumentException(String message) {
        super(message);
    }
}

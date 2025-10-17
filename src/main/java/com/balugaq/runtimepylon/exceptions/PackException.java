package com.balugaq.runtimepylon.exceptions;

/**
 * @author balugaq
 */
public class PackException extends RuntimeException {
    public PackException() {
        super();
    }

    public PackException(String message) {
        super(message);
    }

    public PackException(String message, Throwable cause) {
        super(message, cause);
    }
}

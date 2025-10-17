package com.balugaq.runtimepylon.exceptions;

/**
 * @author balugaq
 */
public class UnknownItemException extends RuntimeException {
    public UnknownItemException() {
        super();
    }

    public UnknownItemException(String message) {
        super(message);
    }
}

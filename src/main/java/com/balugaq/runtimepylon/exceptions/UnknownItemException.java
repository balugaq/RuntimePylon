package com.balugaq.runtimepylon.exceptions;

public class UnknownItemException extends RuntimeException {
    public UnknownItemException() {
        super();
    }

    public UnknownItemException(String message) {
        super(message);
    }
}

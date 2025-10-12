package com.balugaq.runtimepylon.exceptions;

public class UnknownPageException extends RuntimeException {
    public UnknownPageException() {
        super();
    }

    public UnknownPageException(String message) {
        super(message);
    }
}

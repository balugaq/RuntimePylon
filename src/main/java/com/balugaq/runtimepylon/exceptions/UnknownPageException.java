package com.balugaq.runtimepylon.exceptions;

/**
 * @author balugaq
 */
public class UnknownPageException extends RuntimeException {
    public UnknownPageException() {
        super();
    }

    public UnknownPageException(String message) {
        super(message);
    }
}

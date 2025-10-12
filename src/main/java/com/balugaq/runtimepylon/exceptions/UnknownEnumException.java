package com.balugaq.runtimepylon.exceptions;

public class UnknownEnumException extends RuntimeException {
    public UnknownEnumException() {
        super();
    }

    public UnknownEnumException(Class<?> e, String id) {
        super("Unknown element: " + e.getSimpleName() + "." + id);
    }
}

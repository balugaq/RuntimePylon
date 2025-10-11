package com.balugaq.runtimepylon.exceptions;

public class DeserializationException extends PackException {
    public DeserializationException() {
        super();
    }

    public DeserializationException(String message) {
        super(message);
    }

    public <T> DeserializationException(Class<T> clazz) {
        super("An exception occured when trying to deserialize " + clazz.getName());
    }

    public <T> DeserializationException(Class<T> clazz, Exception e) {
        super("An exception occured when trying to deserialize " + clazz.getName(), e);
    }
}

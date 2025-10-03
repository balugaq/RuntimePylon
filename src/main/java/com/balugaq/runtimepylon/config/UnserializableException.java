package com.balugaq.runtimepylon.config;

public class UnserializableException extends RuntimeException {
    public <T> UnserializableException(Class<T> clazz) {
        super("An exception occured when trying to unserialize " + clazz.getName());
    }

    public <T> UnserializableException(Class<T> clazz, Exception e) {
        super("An exception occured when trying to unserialize " + clazz.getName(), e);
    }
}

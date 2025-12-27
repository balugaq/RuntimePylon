package com.balugaq.runtimepylon.exceptions;

import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public class UnknownDeserializerException extends RuntimeException {
    public UnknownDeserializerException() {
        super();
    }

    public UnknownDeserializerException(String message) {
        super(message);
    }

    public UnknownDeserializerException(List<String> parts) {
        super("Unknown deserializer: " + parts);
    }

    public UnknownDeserializerException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public class WrongEnumDeserializerException extends RuntimeException {
    public WrongEnumDeserializerException() {
        super();
    }

    public WrongEnumDeserializerException(String message) {
        super(message);
    }

    public WrongEnumDeserializerException(List<String> parts) {
        super("Wrong enum deserializer: " + parts);
    }

    public WrongEnumDeserializerException(String message, Throwable cause) {
        super(message, cause);
    }
}

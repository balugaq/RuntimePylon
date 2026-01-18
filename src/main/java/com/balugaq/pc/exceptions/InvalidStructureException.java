package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public class InvalidStructureException extends RuntimeException {
    public InvalidStructureException(List<String> list) {
        super("Invalid: \n - " + String.join("\n - ", list.toArray(new String[0])));
    }
}

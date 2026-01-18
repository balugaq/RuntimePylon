package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class ExamineFailedException extends PackException {
    public ExamineFailedException(String message) {
        super(message);
    }
}

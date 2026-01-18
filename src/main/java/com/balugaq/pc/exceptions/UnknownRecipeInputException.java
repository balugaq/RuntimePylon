package com.balugaq.pc.exceptions;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class UnknownRecipeInputException extends RuntimeException {
    public UnknownRecipeInputException() {
        super();
    }

    public UnknownRecipeInputException(String message) {
        super(message);
    }
}

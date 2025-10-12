package com.balugaq.runtimepylon.exceptions;

import com.balugaq.runtimepylon.config.PackDesc;

public class UnknownPageException extends RuntimeException {
    public UnknownPageException() {
        super();
    }

    public UnknownPageException(String message) {
        super(message);
    }
}

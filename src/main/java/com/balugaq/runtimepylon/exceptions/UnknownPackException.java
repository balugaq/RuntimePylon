package com.balugaq.runtimepylon.exceptions;

import com.balugaq.runtimepylon.config.PackDesc;

/**
 * @author balugaq
 */
public class UnknownPackException extends RuntimeException {
    public UnknownPackException() {
        super();
    }

    public UnknownPackException(PackDesc packDesc) {
        super(packDesc.getId());
    }
}

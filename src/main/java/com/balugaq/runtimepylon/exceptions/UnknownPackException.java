package com.balugaq.runtimepylon.exceptions;

import com.balugaq.runtimepylon.config.PackDesc;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class UnknownPackException extends RuntimeException {
    public UnknownPackException() {
        super();
    }

    public UnknownPackException(PackDesc packDesc) {
        super(packDesc.getId());
    }
}

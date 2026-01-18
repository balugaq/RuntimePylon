package com.balugaq.pc.exceptions;

import com.balugaq.pc.config.PackDesc;
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

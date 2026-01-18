package com.balugaq.pc.exceptions;

import com.balugaq.pc.config.Pack;
import com.balugaq.pc.config.PackDesc;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public class PackDependencyMissingException extends RuntimeException {
    public PackDependencyMissingException() {
        super();
    }

    public PackDependencyMissingException(Pack pack, List<PackDesc> packDesc) {
        super("Unable to load " + pack.getPackID() + " as missing Pack dependency " + packDesc);
    }
}

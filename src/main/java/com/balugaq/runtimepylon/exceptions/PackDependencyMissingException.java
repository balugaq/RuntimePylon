package com.balugaq.runtimepylon.exceptions;

import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PackDesc;
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

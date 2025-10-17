package com.balugaq.runtimepylon.exceptions;

import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PackDesc;

import java.util.List;

/**
 * @author balugaq
 */
public class PackDependencyMissingException extends RuntimeException {
    public PackDependencyMissingException() {
        super();
    }

    public PackDependencyMissingException(Pack pack, List<PackDesc> packDesc) {
        super("Unable to load " + pack.getPackID() + " as missing Pack dependency " + packDesc);
    }
}

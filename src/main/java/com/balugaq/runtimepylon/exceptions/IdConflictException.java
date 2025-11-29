package com.balugaq.runtimepylon.exceptions;

import com.balugaq.runtimepylon.config.pack.PackID;
import org.jspecify.annotations.NullMarked;

import java.io.File;

/**
 * @author balugaq
 */
@NullMarked
public class IdConflictException extends RuntimeException {
    public IdConflictException(PackID id, File existing, File reading) {
        super(id.getId() + " is used in " + existing + " and " + reading);
    }
}

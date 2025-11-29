package com.balugaq.runtimepylon.exceptions;

import com.balugaq.runtimepylon.config.PackDesc;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class SaveditemsNotFoundException extends RuntimeException {
    public SaveditemsNotFoundException() {
        super();
    }

    public SaveditemsNotFoundException(PackDesc packDesc) {
        super(packDesc.getId());
    }
}

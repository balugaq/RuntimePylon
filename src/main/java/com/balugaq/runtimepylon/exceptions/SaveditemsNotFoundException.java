package com.balugaq.runtimepylon.exceptions;

import com.balugaq.runtimepylon.config.PackDesc;

/**
 * @author balugaq
 */
public class SaveditemsNotFoundException extends RuntimeException {
    public SaveditemsNotFoundException() {
        super();
    }

    public SaveditemsNotFoundException(PackDesc packDesc) {
        super(packDesc.getId());
    }
}

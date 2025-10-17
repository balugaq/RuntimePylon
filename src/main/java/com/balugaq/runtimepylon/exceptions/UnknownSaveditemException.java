package com.balugaq.runtimepylon.exceptions;

import com.balugaq.runtimepylon.config.PackDesc;
import com.balugaq.runtimepylon.config.SaveditemDesc;

/**
 * @author balugaq
 */
public class UnknownSaveditemException extends UnknownItemException {
    public UnknownSaveditemException() {
        super();
    }

    public UnknownSaveditemException(PackDesc packDesc, SaveditemDesc itemDesc) {
        super(packDesc.getId() + "/" + itemDesc.getFile());
    }

    public UnknownSaveditemException(String message) {
        super(message);
    }
}

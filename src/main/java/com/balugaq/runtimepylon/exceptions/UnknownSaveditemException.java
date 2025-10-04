package com.balugaq.runtimepylon.exceptions;

import com.balugaq.runtimepylon.config.PackDesc;
import com.balugaq.runtimepylon.config.SaveditemDesc;

public class UnknownSaveditemException extends RuntimeException {
    public UnknownSaveditemException() {
        super();
    }

    public UnknownSaveditemException(PackDesc packDesc, SaveditemDesc itemDesc) {
        super(packDesc.getId() + "/" + itemDesc.getFileName());
    }
}

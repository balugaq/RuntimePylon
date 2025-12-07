package com.balugaq.runtimepylon.exceptions;

import com.balugaq.runtimepylon.config.PackDesc;
import com.balugaq.runtimepylon.config.SaveditemDesc;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class UnknownRecipeTypeException extends UnknownItemException {
    public UnknownRecipeTypeException() {
        super();
    }

    public UnknownRecipeTypeException(PackDesc packDesc, SaveditemDesc itemDesc) {
        super(packDesc.getId() + "/" + itemDesc.getFile());
    }

    public UnknownRecipeTypeException(String message) {
        super(message);
    }
}

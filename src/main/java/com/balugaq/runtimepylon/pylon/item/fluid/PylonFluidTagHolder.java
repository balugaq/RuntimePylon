package com.balugaq.runtimepylon.pylon.item.fluid;

import com.balugaq.runtimepylon.pylon.item.DataStack;
import io.github.pylonmc.pylon.core.fluid.PylonFluidTag;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public interface PylonFluidTagHolder<T extends PylonFluidTag> extends DataStack {
    T getTag();
}

package com.balugaq.runtimepylon.pylon.item.fluid;

import com.balugaq.runtimepylon.pylon.item.DataStack;
import io.github.pylonmc.pylon.core.fluid.PylonFluidTag;
import org.jetbrains.annotations.NotNull;

public interface PylonFluidTagHolder<T extends PylonFluidTag> extends DataStack {
    @NotNull T getTag();
}

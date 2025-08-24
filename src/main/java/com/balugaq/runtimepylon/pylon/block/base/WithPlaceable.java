package com.balugaq.runtimepylon.pylon.block.base;

import org.jetbrains.annotations.NotNull;

public interface WithPlaceable {
    boolean isPlaceable();

    @NotNull WithPlaceable setPlaceable(boolean placeable);
}

package com.balugaq.runtimepylon.block.base;

import org.jetbrains.annotations.NotNull;

public interface WithPlaceable {
    boolean isPlaceable();

    @NotNull WithPlaceable setPlaceable(boolean placeable);
}

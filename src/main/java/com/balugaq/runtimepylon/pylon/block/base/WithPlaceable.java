package com.balugaq.runtimepylon.pylon.block.base;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public interface WithPlaceable {
    boolean isPlaceable();

    void setPlaceable(boolean placeable);
}

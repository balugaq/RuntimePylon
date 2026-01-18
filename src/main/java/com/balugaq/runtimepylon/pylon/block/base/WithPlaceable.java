package com.balugaq.runtimepylon.pylon.block.base;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
@Deprecated
public interface WithPlaceable {
    boolean isPlaceable();

    void setPlaceable(boolean placeable);
}

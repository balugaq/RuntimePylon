package com.balugaq.runtimepylon.pylon.block.base;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
@Deprecated
public interface WithPage extends WithModel {
    @Nullable NamespacedKey getPageId();

    void setPageId(NamespacedKey key);
}

package com.balugaq.runtimepylon.object;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.item.Item;

import java.util.function.Supplier;

/**
 * @author balugaq
 */
@NullMarked
@FunctionalInterface
public interface ItemStackProvider {
    Supplier<Item> display(char c, @Nullable CustomRecipe recipe);
}

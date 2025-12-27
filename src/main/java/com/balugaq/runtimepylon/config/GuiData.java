package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.object.CustomRecipe;
import com.balugaq.runtimepylon.object.ItemStackProvider;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public record GuiData(
        NamespacedKey key,
        List<String> structure,
        @Nullable ItemStackProvider provider,
        Gui.Builder.Normal builder,
        @Nullable CustomRecipe recipe,
        List<Character> invSlotChars
) {
}

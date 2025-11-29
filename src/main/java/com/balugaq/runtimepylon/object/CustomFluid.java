package com.balugaq.runtimepylon.object;

import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.PylonFluidTag;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public class CustomFluid extends PylonFluid {
    public CustomFluid(final NamespacedKey key, final Component name, final Material material, final List<PylonFluidTag> tags) {
        super(key, name, material, tags);
    }

    public CustomFluid(final NamespacedKey key, final Material material, final PylonFluidTag... tags) {
        super(key, material, tags);
    }
}

package com.balugaq.runtimepylon.object;

import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.pages.base.GuidePage;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.item.research.Research;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.item.Item;

import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public class CustomGuidePage extends SimpleStaticGuidePage {
    public CustomGuidePage(final NamespacedKey key, final Material material, final List<Item> buttons) {
        super(key, material, buttons);
    }

    public CustomGuidePage(final NamespacedKey key, final Material material) {
        super(key, material);
    }

    @Override
    public boolean addItem(ItemStack item) {
        return getButtons().add(new CustomItemButton(getKey(), item));
    }

    @Override
    public boolean addFluid(PylonFluid fluid) {
        return getButtons().add(new CustomFluidButton(getKey(), fluid));
    }

    @Override
    public boolean addResearch(Research research) {
        return getButtons().add(new CustomResearchButton(getKey(), research));
    }

    @Override
    public boolean addPage(GuidePage page) {
        return getButtons().add(new CustomPageButton(getKey(), page));
    }
}

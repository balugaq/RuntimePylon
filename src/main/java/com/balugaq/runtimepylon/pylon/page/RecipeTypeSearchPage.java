package com.balugaq.runtimepylon.pylon.page;

import com.balugaq.runtimepylon.gui.GuiItem;
import com.balugaq.runtimepylon.pylon.RuntimeKeys;
import io.github.pylonmc.pylon.core.guide.pages.base.SearchPage;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import kotlin.Pair;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.item.Item;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author balugaq
 */
@Getter
@NullMarked
public class RecipeTypeSearchPage extends SearchPage {
    private final Consumer<RecipeType<? extends PylonRecipe>> consumer;

    public RecipeTypeSearchPage(Consumer<RecipeType<? extends PylonRecipe>> consumer) {
        super(RuntimeKeys.recipe_type_search_page, Material.STONE);
        this.consumer = consumer;
    }

    @Override
    public List<Pair<Item, String>> getItemNamePairs(Player player, String search) {
        return PylonRegistry.RECIPE_TYPES.getValues()
                .stream()
                .map(type -> new Pair<>(
                        (Item) GuiItem.create(null) // unused null
                                .item(block -> ItemStackBuilder.of(Material.CRAFTING_TABLE)
                                        .name(type.getKey().toString()))
                                .click((block, clickType, p2, event) -> {
                                    consumer.accept(type);
                                    SearchPages.triggerBackGuide(player, false);
                                    return true;
                                }),
                        type.getKey().toString()
                ))
                .toList();
    }
}

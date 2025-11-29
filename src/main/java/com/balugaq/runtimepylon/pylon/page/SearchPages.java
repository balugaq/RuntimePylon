package com.balugaq.runtimepylon.pylon.page;

import com.balugaq.runtimepylon.pylon.RuntimeKeys;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.pages.base.GuidePage;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author balugaq
 */
@NullMarked
public class SearchPages {
    public static void openPageSearchPage(Player player, Consumer<SimpleStaticGuidePage> consumer) {
        new PageSearchPage(consumer).open(player);
    }

    public static void openRecipeTypeSearchPage(Player player, Consumer<RecipeType<? extends PylonRecipe>> consumer) {
        new RecipeTypeSearchPage(consumer).open(player);
    }

    public static void triggerBackGuide(Player player, boolean open) {
        var history = PylonGuide.getHistory().getOrDefault(player.getUniqueId(), List.of());
        if (!history.isEmpty() && isTaggedPage(history.getLast())) {
            history.removeLast();
            if (open) history.getLast().open(player);
        }
    }

    public static boolean isTaggedPage(GuidePage page) {
        return page.getKey().equals(RuntimeKeys.page_search_page) || page.getKey().equals(RuntimeKeys.recipe_type_search_page);
    }
}

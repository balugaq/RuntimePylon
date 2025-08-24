package com.balugaq.runtimepylon.pylon.page;

import com.balugaq.runtimepylon.pylon.RuntimeKeys;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.pages.base.GuidePage;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class SearchPages {
    public static void openPageSearchPage(@NotNull Player player, @NotNull Consumer<SimpleStaticGuidePage> consumer) {
        new PageSearchPage(consumer).open(player);
    }

    public static void openRecipeTypeSearchPage(@NotNull Player player, @NotNull Consumer<RecipeType<? extends PylonRecipe>> consumer) {
        new RecipeTypeSearchPage(consumer).open(player);
    }

    public static void triggerBackGuide(@NotNull Player player, boolean open) {
        var history = PylonGuide.getHistory().getOrDefault(player.getUniqueId(), List.of());
        if (!history.isEmpty() && isTaggedPage(history.getLast())) {
            history.removeLast();
            if (open) history.getLast().open(player);
        }
    }

    public static boolean isTaggedPage(@NotNull GuidePage page) {
        return page.getKey().equals(RuntimeKeys.page_search_page) || page.getKey().equals(RuntimeKeys.recipe_type_search_page);
    }
}

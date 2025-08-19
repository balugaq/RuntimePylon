package com.balugaq.runtimepylon.gui;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.util.Key;
import io.github.pylonmc.pylon.core.guide.pages.base.SearchPage;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import kotlin.Pair;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Item;

import java.util.List;
import java.util.function.Consumer;

public class SearchPages {
    public static void openGroupSearchPage(@NotNull Player player, @NotNull Consumer<SimpleStaticGuidePage> consumer) {
        new GroupSearchPage(consumer).open(player);
    }

    public static void openRecipeTypeSearchPage(@NotNull Player player, @NotNull Consumer<RecipeType<? extends PylonRecipe>> consumer) {
        new RecipeTypeSearchPage(consumer).open(player);
    }

    @Getter
    public static class GroupSearchPage extends SearchPage {
        private final @NotNull Consumer<SimpleStaticGuidePage> consumer;

        public GroupSearchPage(@NotNull Consumer<SimpleStaticGuidePage> consumer) {
            super(Key.create("group_search_page"), Material.STONE);
            this.consumer = consumer;
        }

        @Override
        public @NotNull List<Pair<Item, String>> getItemNamePairs(@NotNull Player player, @NotNull String search) {
            return RuntimePylon.getGuidePages().values()
                    .stream()
                    .map(page -> {
                        String name = PlainTextComponentSerializer.plainText().serialize(GlobalTranslator.render(
                                Component.translatable("pylon.${item.namespace}.item.${item.key}.name"),
                                player.locale()
                        ));

                        return new Pair<>(
                                (Item) GuiItem.create(null)
                                        .item(block -> page.getItem())
                                        .click((block, clickType, p2, event) -> {
                                            consumer.accept(page);
                                            return true;
                                        }),
                                name);
                    })
                    .toList();
        }
    }

    @Getter
    public static class RecipeTypeSearchPage extends SearchPage {
        private final @NotNull Consumer<RecipeType<? extends PylonRecipe>> consumer;

        public RecipeTypeSearchPage(@NotNull Consumer<RecipeType<? extends PylonRecipe>> consumer) {
            super(Key.create("recipe_type_search_page"), Material.STONE);
            this.consumer = consumer;
        }

        @Override
        public @NotNull List<Pair<Item, String>> getItemNamePairs(@NotNull Player player, @NotNull String search) {
            return PylonRegistry.RECIPE_TYPES.getValues()
                    .stream()
                    .map(type -> {
                        return new Pair<>(
                                (Item) GuiItem.create(null)
                                        .item(block -> ItemStackBuilder.of(Material.CRAFTING_TABLE)
                                                .name(type.getKey().toString()))
                                        .click((block, clickType, p2, event) -> {
                                            consumer.accept(type);
                                            return true;
                                        }),
                                type.getKey().toString());
                    })
                    .toList();
        }
    }
}

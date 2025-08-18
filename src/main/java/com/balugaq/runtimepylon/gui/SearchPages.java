package com.balugaq.runtimepylon.gui;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.util.Key;
import io.github.pylonmc.pylon.core.guide.pages.base.SearchPage;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.papermc.paper.datacomponent.DataComponentTypes;
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

    @Getter
    public static class GroupSearchPage extends SearchPage {
        private final Consumer<SimpleStaticGuidePage> consumer;
        public GroupSearchPage(@NotNull Consumer<SimpleStaticGuidePage> consumer) {
            super(Key.create("group_search_page"), Material.STONE);
            this.consumer = consumer;
        }

        @Override
        public @NotNull List<Pair<Item, String>> getItemNamePairs(@NotNull Player player, @NotNull String search) {
            Component target = Component.text(search);
            return RuntimePylon.getGuidePages().values()
                    .stream()
                    .filter(page -> {
                        Component title1 = page.getTitle();
                        if (title1.contains(target)) return true;
                        Component title2 = page.getItem().get().getData(DataComponentTypes.ITEM_NAME);
                        return title2 != null && title2.contains(target);
                    })
                    .map(page -> {
                        String name = PlainTextComponentSerializer.plainText().serialize(GlobalTranslator.render(
                                Component.translatable("pylon.${item.namespace}.item.${item.key}.name"),
                                player.locale()
                        ));

                        return new Pair<>(
                                (Item) GuiItem.create(null)
                                        .item(page.getItem())
                                        .click((block, clickType, p2, event) -> {
                                            consumer.accept(page);
                                        }),
                                name);
                    })
                    .toList();
        }
    }
}

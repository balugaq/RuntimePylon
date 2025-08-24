package com.balugaq.runtimepylon.pylon.page;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.gui.GuiItem;
import com.balugaq.runtimepylon.pylon.RuntimeKeys;
import io.github.pylonmc.pylon.core.guide.pages.base.SearchPage;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
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

@Getter
public class PageSearchPage extends SearchPage {
    private final @NotNull Consumer<SimpleStaticGuidePage> consumer;

    public PageSearchPage(@NotNull Consumer<SimpleStaticGuidePage> consumer) {
        super(RuntimeKeys.page_search_page, Material.STONE);
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
                            (Item) GuiItem.create(null) // unused null
                                    .item(block -> page.getItem())
                                    .click((block, clickType, p2, event) -> {
                                        consumer.accept(page);
                                        SearchPages.triggerBackGuide(player, false);
                                        return true;
                                    }),
                            name);
                })
                .toList();
    }
}

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
public class PageSearchPage extends SearchPage {
    private final Consumer<SimpleStaticGuidePage> consumer;

    public PageSearchPage(Consumer<SimpleStaticGuidePage> consumer) {
        super(RuntimeKeys.page_search_page);
        this.consumer = consumer;
    }

    @Override
    public List<Pair<Item, String>> getItemNamePairs(Player player, String search) {
        return RuntimePylon.getGuidePages().values()
                .stream()
                .map(page -> {
                    String name = PlainTextComponentSerializer.plainText().serialize(GlobalTranslator.render(
                            Component.translatable("pylon.${item.namespace}.item.${item.key}.name"),
                            player.locale()
                    ));

                    return new Pair<>(
                            (Item) GuiItem.create(null) // unused null
                                    .item(block -> page.getItemProvider())
                                    .click((block, clickType, p2, event) -> {
                                        if (page.getPage() instanceof SimpleStaticGuidePage ssg) {
                                            consumer.accept(ssg);
                                            SearchPages.triggerBackGuide(player, false);
                                            return true;
                                        }
                                        return false;
                                    }),
                            name
                    );
                })
                .toList();
    }
}

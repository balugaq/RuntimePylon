package com.balugaq.runtimepylon.pylon.block;

import com.balugaq.runtimepylon.pylon.MyBlock;
import com.balugaq.runtimepylon.pylon.RuntimeKeys;
import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.pylon.block.base.WithModel;
import com.balugaq.runtimepylon.gui.ButtonSet;
import com.balugaq.runtimepylon.pylon.page.SearchPages;
import com.balugaq.runtimepylon.util.Key;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.guide.button.PageButton;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import static com.balugaq.runtimepylon.util.Lang.*;
import static com.balugaq.runtimepylon.gui.GuiItem.toNamespacedKey;
import static com.balugaq.runtimepylon.gui.GuiItem.waitInput;

@Getter
public class PageHub extends MyBlock implements
        PylonGuiBlock,
        WithModel {
    public @Nullable ItemStack model = null;
    public @Nullable NamespacedKey pageId = null;
    public @Nullable NamespacedKey nestedPageId = null;
    public boolean displayInRoot = true;

    public PageHub(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public PageHub(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        model = pdc.get(RuntimeKeys.model, PylonSerializers.ITEM_STACK);
        pageId = pdc.get(RuntimeKeys.item_id, PylonSerializers.NAMESPACED_KEY);
        nestedPageId = pdc.get(RuntimeKeys.nested_page_id, PylonSerializers.NAMESPACED_KEY);
        displayInRoot = pdc.getOrDefault(RuntimeKeys.display_in_root, PylonSerializers.BOOLEAN, true);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        super.write(pdc);
        if (model != null) pdc.set(RuntimeKeys.model, PylonSerializers.ITEM_STACK, model);
        if (pageId != null) pdc.set(RuntimeKeys.item_id, PylonSerializers.NAMESPACED_KEY, pageId);
        if (nestedPageId != null) pdc.set(RuntimeKeys.nested_page_id, PylonSerializers.NAMESPACED_KEY, nestedPageId);
        pdc.set(RuntimeKeys.display_in_root, PylonSerializers.BOOLEAN, displayInRoot);
    }

    @Override
    public @NotNull Gui createGui() {
        PageHubButtonSet<?> buttons = new PageHubButtonSet<>(this);
        return Gui.normal()
                .setStructure(
                        "x x x x x x x x x",
                        ". k . e . . . . .",
                        ". i . g . . . . .",
                        ". s . d . . . . .",
                        "x x x x x x x x x"
                )
                .addIngredient('x', buttons.blackBackground)
                .addIngredient('.', buttons.grayBackground)
                .addIngredient('e', buttons.setNestedPage)
                .addIngredient('d', buttons.unsetNestedPage)
                .addIngredient('k', buttons.setId)
                .addIngredient('g', buttons.nestedPage)
                .addIngredient('i', buttons.item)
                .addIngredient('s', buttons.registerPage)
                .build();
    }

    @Override
    public @NotNull WithModel setModel(@Nullable ItemStack model) {
        this.model = model;
        return this;
    }

    @Override
    public @Nullable NamespacedKey getItemId() {
        return pageId;
    }

    @Override
    public @NotNull WithModel setItemId(@Nullable NamespacedKey itemId) {
        this.pageId = itemId;
        return this;
    }


    @Getter
    public static class PageHubButtonSet<T extends PageHub> extends ButtonSet<T> {
        public final @NotNull AbstractItem
                registerPage,
                setNestedPage,
                unsetNestedPage,
                nestedPage;

        public PageHubButtonSet(@NotNull T b2) {
            super(b2);

            registerPage = create()
                    .item(data -> ItemStackBuilder.pylonItem(
                            Material.EMERALD_BLOCK,
                            RuntimeKeys.register_page
                    ))
                    .click((data, clickType, player, event) -> {
                        assertNotNull(data.pageId, register_page_1);
                        assertNotNull(data.model, register_page_2);
                        assertTrue(!data.model.getType().isAir(), register_page_3);

                        SimpleStaticGuidePage page = new SimpleStaticGuidePage(data.pageId, data.model.getType());
                        if (data.displayInRoot) PylonGuide.getRootPage().addPage(page);
                        done(player, register_page_4, data.pageId);
                        return false;
                    });

            nestedPage = create()
                    .item(data -> {
                        if (data.nestedPageId == null) {
                            return ItemStackBuilder.pylonItem(
                                    Material.WHITE_STAINED_GLASS_PANE,
                                    RuntimeKeys.nested_page
                            );
                        } else {
                            return RuntimePylon.getGuidePages().get(data.getNestedPageId()).getItem();
                        }
                    })
                    .click((data, clickType, player, event) -> {
                        if (clickType.isLeftClick()) {
                            if (clickType.isShiftClick()) {
                                waitInput(player, nested_page_1, pageId -> {
                                    data.nestedPageId = assertNotNull(toNamespacedKey(pageId), nested_page_2);
                                });
                            } else {
                                SearchPages.openPageSearchPage(player, page -> {
                                    data.nestedPageId = page.getKey();
                                    done(player, nested_page_3, page.getKey());
                                    reopen(player);
                                });
                            }
                        } else if (clickType.isRightClick()) {
                            assertNotNull(data.nestedPageId, nested_page_4);
                            // copy id

                            player.sendMessage(Component.text()
                                    .content(nested_page_5)
                                    .hoverEvent(HoverEvent.showText(Component.text(nested_page_6)))
                                    .clickEvent(ClickEvent.copyToClipboard(data.nestedPageId.toString())));
                        }

                        return true;
                    });

            setNestedPage = create()
                    .item(data -> ItemStackBuilder.pylonItem(
                            Material.GREEN_STAINED_GLASS_PANE,
                            RuntimeKeys.set_nested_page
                    ))
                    .click((data, clickType, player, event) -> {
                        assertNotNull(data.pageId, set_nested_page_1);
                        assertNotNull(data.nestedPageId, set_nested_page_2);
                        SimpleStaticGuidePage page = assertNotNull(RuntimePylon.getGuidePages().get(data.pageId), set_nested_page_3);
                        SimpleStaticGuidePage nestedPage = assertNotNull(RuntimePylon.getGuidePages().get(data.nestedPageId), set_nested_page_4);
                        nestedPage.addPage(page);
                        done(player, set_nested_page_5, data.nestedPageId);
                        return false;
                    });

            unsetNestedPage = create()
                    .item(data -> ItemStackBuilder.pylonItem(
                            Material.RED_STAINED_GLASS_PANE,
                            RuntimeKeys.unset_nested_page
                    ))
                    .click((data, clickType, player, event) -> {
                        assertNotNull(data.pageId, unset_nested_page_1);
                        assertNotNull(data.nestedPageId, unset_nested_page_2);
                        SimpleStaticGuidePage page = assertNotNull(RuntimePylon.getGuidePages().get(data.pageId), unset_nested_page_3);
                        SimpleStaticGuidePage nestedPage = assertNotNull(RuntimePylon.getGuidePages().get(data.nestedPageId), unset_nested_page_4);
                        synchronized (nestedPage.getButtons()) {
                            nestedPage.getButtons().removeIf(button -> button instanceof PageButton pb && pb.getPage() == page);
                        }
                        done(player, unset_nested_page_5);
                        return false;
                    });
        }
    }
}

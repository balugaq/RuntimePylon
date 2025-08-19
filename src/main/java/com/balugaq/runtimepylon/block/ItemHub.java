package com.balugaq.runtimepylon.block;

import com.balugaq.runtimepylon.block.base.WithGroup;
import com.balugaq.runtimepylon.block.base.WithModel;
import com.balugaq.runtimepylon.block.base.WithRecipe;
import com.balugaq.runtimepylon.gui.ButtonSet;
import com.balugaq.runtimepylon.util.Key;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import kotlin.Pair;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import xyz.xenondevs.invui.gui.Gui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class ItemHub extends PylonBlock implements
        PylonGuiBlock,
        WithModel,
        WithGroup,
        WithRecipe {
    public @Nullable ItemStack model = null;
    public @Nullable NamespacedKey itemId = null;
    public @Nullable NamespacedKey groupId = null;
    public @Nullable NamespacedKey recipeTypeId = null;
    public @NotNull Map<Integer, ItemStack> recipe = new HashMap<>();

    public ItemHub(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public ItemHub(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        model = getItemStack(pdc, "model");
        itemId = getNamespacedKey(pdc, "itemId");
        groupId = getNamespacedKey(pdc, "groupId");
        recipeTypeId = getNamespacedKey(pdc, "recipeTypeId");
        recipe = fromArray(pdc, "recipe");
    }

    public static @NotNull Map<Integer, ItemStack> fromArray(@NotNull PersistentDataContainer pdc, @NotNull String key) {
        return pdc.getKeys().stream().filter(k -> k.toString().startsWith(key))
                .map(k -> new Pair<>(k, pdc.get(k, PersistentDataType.STRING)))
                .collect(Collectors.toMap(
                        p -> Integer.parseInt(p.getFirst().toString().substring(key.length())),
                        p -> getItemStack(p.getSecond())
                ));
    }

    public static @Nullable NamespacedKey getNamespacedKey(@NotNull PersistentDataContainer pdc, @NotNull String key) {
        var s = pdc.get(Key.create(key), PersistentDataType.STRING);
        if (s == null) return null;
        return NamespacedKey.fromString(s);
    }

    public static @Nullable ItemStack getItemStack(@NotNull PersistentDataContainer pdc, @NotNull String key) {
        var s = pdc.get(Key.create(key), PersistentDataType.STRING);
        if (s == null) return null;
        return getItemStack(s);
    }

    @SuppressWarnings("deprecation")
    public static @NotNull String getBase64String(ItemStack item) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            BukkitObjectOutputStream bs = new BukkitObjectOutputStream(stream);
            bs.writeObject(item);
            bs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64Coder.encodeLines(stream.toByteArray());
    }

    @SuppressWarnings("deprecation")
    public static @Nullable ItemStack getItemStack(@NotNull String base64Str) {
        ByteArrayInputStream stream = new ByteArrayInputStream(Base64Coder.decodeLines(base64Str));
        try {
            BukkitObjectInputStream bs = new BukkitObjectInputStream(stream);
            ItemStack re = (ItemStack) bs.readObject();
            bs.close();
            return re;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        super.write(pdc);
        pdc.set(Key.create("model"), PersistentDataType.STRING, getBase64String(model));
        if (itemId != null) pdc.set(Key.create("itemId"), PersistentDataType.STRING, itemId.toString());
        if (groupId != null) pdc.set(Key.create("groupId"), PersistentDataType.STRING, groupId.toString());
        if (recipeTypeId != null)
            pdc.set(Key.create("recipeTypeId"), PersistentDataType.STRING, recipeTypeId.toString());
        recipe.forEach((key, value) -> pdc.set(Key.create("recipe" + key), PersistentDataType.STRING, getBase64String(value)));
    }

    @Override
    public @NotNull Gui createGui() {
        ButtonSet buttons = new ButtonSet(this);
        return Gui.normal()
                .setStructure(
                        "x x x x x x x x x",
                        ". k . e E 1 2 3 .",
                        ". i . g t 4 5 6 .",
                        ". s . d D 7 8 9 .",
                        "x x x x x x x x x"
                )
                .addIngredient('x', buttons.blackBackground)
                .addIngredient('.', buttons.grayBackground)
                .addIngredient('e', buttons.setItemGroup)
                .addIngredient('E', buttons.setRecipe)
                .addIngredient('d', buttons.unsetItemGroup)
                .addIngredient('D', buttons.unsetRecipe)
                .addIngredient('k', buttons.setId)
                .addIngredient('g', buttons.itemGroup)
                .addIngredient('t', buttons.recipeType)
                .addIngredient('i', buttons.item)
                .addIngredient('1', buttons.recipe(1))
                .addIngredient('2', buttons.recipe(2))
                .addIngredient('3', buttons.recipe(3))
                .addIngredient('4', buttons.recipe(4))
                .addIngredient('5', buttons.recipe(5))
                .addIngredient('6', buttons.recipe(6))
                .addIngredient('7', buttons.recipe(7))
                .addIngredient('8', buttons.recipe(8))
                .addIngredient('9', buttons.recipe(9))
                .addIngredient('s', buttons.register)
                .build();
    }

    @Override
    public @NotNull WithGroup setGroupId(@NotNull NamespacedKey key) {
        this.groupId = key;
        return this;
    }

    @Override
    public @NotNull WithRecipe setRecipeTypeId(@Nullable NamespacedKey recipeTypeId) {
        this.recipeTypeId = recipeTypeId;
        return this;
    }

    @Override
    public @NotNull WithRecipe setRecipe(@NotNull Map<Integer, ItemStack> recipe) {
        this.recipe = recipe;
        return this;
    }

    @Override
    public @NotNull WithModel setModel(@Nullable ItemStack model) {
        this.model = model;
        return this;
    }

    @Override
    public @NotNull WithModel setItemId(@Nullable NamespacedKey itemId) {
        this.itemId = itemId;
        return this;
    }
}

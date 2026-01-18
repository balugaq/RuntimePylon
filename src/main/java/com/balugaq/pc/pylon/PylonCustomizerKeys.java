package com.balugaq.pc.pylon;

import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import static com.balugaq.pc.util.Keys.create;

/**
 * @author balugaq
 */
@NullMarked
public class PylonCustomizerKeys {
    // @formatter:off
    public static final NamespacedKey

    item_hub = create("item_hub"),
    number_stack = create("number_stack"),
    string_stack = create("string_stack"),
    black_background = create("black_background"),
    gray_background = create("gray_background"),
    set_page = create("set_page"),
    set_recipe = create("set_recipe"),
    unset_page = create("unset_page"),
    unset_recipe = create("unset_recipe"),
    set_id = create("set_id"),
    page = create("page"),
    recipe_type = create("recipe_type"),
    item = create("item"),
    register_item = create("register_item"),
    page_search_page = create("page_search_page"),
    recipe_type_search_page = create("recipe_type_search_page"),
    model = create("model"),
    item_id = create("item_id"),
    page_id = create("page_id"),
    recipeType_id = create("recipeType_id"),
    placeable = create("placeable"),
    placeable_active = create("placeable_active"),
    placeable_inactive = create("placeable_inactive"),
    tag = create("tag"),
    register_fluid = create("register_fluid"),
    set_tag = create("set_tag"),
    unset_tag = create("unset_tag"),
    fluid_hub = create("fluid_hub"),
    input_border = create("input_border"),
    output_border = create("output_border"),
    fluid_temperature_holder = create("fluid_temperature_holder"),
    main = create("main"),
    register_page = create("register_page"),
    set_nested_page = create("set_nested_page"),
    unset_nested_page = create("unset_nested_page"),
    nested_page_id = create("nested_page_id"),
    nested_page = create("nested_page"),
    page_hub = create("page_hub"),
    display_in_root = create("display_in_root"),
    recipe_copier = create("recipe_copier"),
    make_recipe = create("make_recipe")
    ;// @formatter:on
}

package com.balugaq.runtimepylon.object;

import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.item.ItemTypeWrapper;
import io.github.pylonmc.pylon.core.recipe.ConfigurableRecipeType;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author balugaq
 */
@NullMarked
public class CustomRecipeType extends ConfigurableRecipeType<CustomRecipe> {
    public static final Item EMPTY = new SimpleItem(ItemStack.empty());

    /**
     * a~z: input item
     * 1-9:: output item
     * B: background item
     * I: input border
     * O: output border
     */
    public static final ItemStackProvider DEFAULT_GUI_PROVIDER = (c, r) -> {
        if (r != null) {
            if ('a' <= c && c <= 'z') {
                var i = r.getInputs();
                var k = c - 'a';
                if (k >= i.size()) return () -> EMPTY;
                var s = i.get(k);
                if (s instanceof RecipeInput.Item item) return () -> ItemButton.from(item);
                else if (s instanceof RecipeInput.Fluid fluid) return () -> new FluidButton(fluid);
            }
            if ('1' <= c && c <= '9') {
                var o = r.getResults();
                var k = c - '1';
                if (k >= o.size()) return () -> EMPTY;
                var s = o.get(k);
                if (s instanceof FluidOrItem.Item item) return () -> ItemButton.from(item.item());
                else if (s instanceof FluidOrItem.Fluid fluid)
                    return () -> new FluidButton(fluid.amountMillibuckets(), fluid.fluid());
            }
        }
        if (c == 'B') return GuiItems::background;
        if (c == 'I') return GuiItems::input;
        if (c == 'O') return GuiItems::output;
        return () -> EMPTY;
    };
    public static final Map<String, Handler> DEFAULT_CONFIG_READER = Map.of(
            "inputs", new Handler(ConfigAdapter.LIST.from(ConfigAdapter.RECIPE_INPUT), new ArrayList<>()),
            "results", new Handler(ConfigAdapter.LIST.from(ConfigAdapter.FLUID_OR_ITEM), new ArrayList<>())
    );

    private final List<String> structure;
    private final ItemStackProvider provider;
    private final Map<String, Handler> configReader;

    public CustomRecipeType(final NamespacedKey key, List<String> structure, @Nullable ItemStackProvider guiProvider, @Nullable Map<String, Handler> configReader) {
        super(key);
        this.structure = structure;
        this.provider = guiProvider == null ? DEFAULT_GUI_PROVIDER : guiProvider;
        this.configReader = configReader == null ? DEFAULT_CONFIG_READER : configReader;
    }

    public Gui makeGui(Gui.Builder.Normal gui, @Nullable CustomRecipe recipe) {
        return makeGui(structure, provider, gui, recipe);
    }

    public static Gui makeGui(List<String> structure, @Nullable ItemStackProvider provider, Gui.Builder.Normal gui, @Nullable CustomRecipe recipe) {
        var s = structure.toArray(new String[0]);
        gui.setStructure(s);
        CharOpenHashSet set = new CharOpenHashSet();
        for (var s2 : s) {
            for (var c : s2.toCharArray()) {
                set.add(c);
            }
        }
        for (char c : set) {
            gui.addIngredient(c, (provider == null ? DEFAULT_GUI_PROVIDER : provider).display(c, recipe));
        }
        return gui.build();
    }

    protected CustomRecipe loadRecipe(final NamespacedKey key, final ConfigSection section) {
        Map<String, Object> other = new HashMap<>();
        for (Map.Entry<String, Handler> e : configReader.entrySet()) {
            try {
                other.put(e.getKey(), ReflectionUtil.invokeMethod(section, "get", e.getKey(), e.getValue().adapter(), e.getValue().defaultValue()));
            } catch (InvocationTargetException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        List<RecipeInput> inputs = readInputs(other.get("inputs"));
        List<FluidOrItem> results = readResults(other.get("results"));

        return new CustomRecipe(this, key, inputs, results, other);
    }

    private List<FluidOrItem> readResults(Object object) {
        List<FluidOrItem> s = new ArrayList<>();
        for (RecipeInput r : readInputs(object)) {
            if (r instanceof RecipeInput.Item item) {
                for (ItemTypeWrapper wrapper : item.getItems()) {
                    s.add(FluidOrItem.of(wrapper.createItemStack()));
                }
            } else if (r instanceof RecipeInput.Fluid fluid) {
                for (PylonFluid f : fluid.fluids()) {
                    s.add(FluidOrItem.of(f, fluid.amountMillibuckets()));
                }
            }
        }
        return s;
    }

    private List<RecipeInput> readInputs(Object object) {
        List<RecipeInput> s = new ArrayList<>();
        switch (object) {
            case ItemStack stack -> {
                return List.of(RecipeInput.of(stack));
            }
            case PylonFluid fluid -> {
                return List.of(RecipeInput.of(fluid, 1));
            }
            case RecipeInput.Item item -> {
                return List.of(item);
            }
            case RecipeInput.Fluid fluid -> {
                return List.of(fluid);
            }
            case FluidOrItem.Item item -> {
                return List.of(RecipeInput.of(item.item()));
            }
            case FluidOrItem.Fluid fluid -> {
                return List.of(RecipeInput.of(fluid.fluid(), fluid.amountMillibuckets()));
            }
            case List<?> list -> {
                for (Object o : list) {
                    List<RecipeInput> r = readInputs(o);
                    s.addAll(r);
                }
                return s;
            }
            case Set<?> set -> {
                for (Object o : set) {
                    List<RecipeInput> r = readInputs(o);
                    s.addAll(r);
                }
                return s;
            }
            default -> {
            }
        }

        return s;
    }

    @NullMarked
    public record Handler(ConfigAdapter<?> adapter, @Nullable Object defaultValue) implements Deserializer<Handler> {
        public static final Map<String, ConfigAdapter<?>> adapters = new HashMap<>();

        static {
            adapters.put("BYTE", ConfigAdapter.BYTE);
            adapters.put("SHORT", ConfigAdapter.SHORT);
            adapters.put("INT", ConfigAdapter.INT);
            adapters.put("LONG", ConfigAdapter.LONG);
            adapters.put("FLOAT", ConfigAdapter.FLOAT);
            adapters.put("DOUBLE", ConfigAdapter.DOUBLE);
            adapters.put("CHAR", ConfigAdapter.CHAR);
            adapters.put("BOOLEAN", ConfigAdapter.BOOLEAN);
            adapters.put("STRING", ConfigAdapter.STRING);
            adapters.put("ANY", ConfigAdapter.ANY);
            adapters.put("NAMESPACED_KEY", ConfigAdapter.NAMESPACED_KEY);
            adapters.put("ITEM_STACK", ConfigAdapter.ITEM_STACK);
            adapters.put("MATERIAL", ConfigAdapter.MATERIAL);
            adapters.put("BLOCK_DATA", ConfigAdapter.BLOCK_DATA);
            adapters.put("SOUND", ConfigAdapter.SOUND);
            adapters.put("RANDOMIZED_SOUND", ConfigAdapter.RANDOMIZED_SOUND);
            adapters.put("PYLON_FLUID", ConfigAdapter.PYLON_FLUID);
            adapters.put("FLUID_TEMPERATURE", ConfigAdapter.FLUID_TEMPERATURE);
            adapters.put("FLUID_OR_ITEM", ConfigAdapter.FLUID_OR_ITEM);
            adapters.put("RECIPE_INPUT", ConfigAdapter.RECIPE_INPUT);
            adapters.put("RECIPE_INPUT_ITEM", ConfigAdapter.RECIPE_INPUT_ITEM);
            adapters.put("RECIPE_INPUT_FLUID", ConfigAdapter.RECIPE_INPUT_FLUID);
            adapters.put("ITEM_TAG", ConfigAdapter.ITEM_TAG);
            adapters.put("CULLING_PRESET", ConfigAdapter.CULLING_PRESET);
            adapters.put("WAILA_DISPLAY", ConfigAdapter.WAILA_DISPLAY);
        }

        public Handler {
        }

        private static ConfigAdapter<?> readConfigAdapter(List<String> parts) {
            if (parts.isEmpty()) {
                return ConfigAdapter.ANY; // fallback
            }

            String p = parts.getFirst();
            for (var e : adapters.entrySet()) {
                String k = e.getKey();
                if (k.equalsIgnoreCase(p)) {
                    return e.getValue();
                }
            }

            if (p.equalsIgnoreCase("list")) {
                return ConfigAdapter.LIST.from(readConfigAdapter(parts.subList(1, parts.size())));
            }

            if (p.equalsIgnoreCase("set")) {
                return ConfigAdapter.SET.from(readConfigAdapter(parts.subList(1, parts.size())));
            }

            if (p.equalsIgnoreCase("map")) {
                return ConfigAdapter.MAP.from(readConfigAdapter(parts.subList(1, parts.size())), readConfigAdapter(parts.subList(1, parts.size())));
            }

            if (p.equalsIgnoreCase("enum")) {
                if (parts.size() < 2) {
                    return ConfigAdapter.ANY; // fallback
                }
                Class<?> clazz = findClass(parts.get(1));
                if (clazz == null || !clazz.isEnum()) {
                    return ConfigAdapter.ANY;
                }

                return ConfigAdapter.ENUM.from((Class<? extends Enum>) clazz);
            }

            return ConfigAdapter.ANY; // fallback
        }

        @Nullable
        private static Class<?> findClass(String name) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        @Override
        public List<ConfigReader<?, Handler>> readers() {
            return List.of(ConfigReader.of(
                    String.class, s -> {
                        String[] a = s.split(";", 1);
                        if (a.length == 0) {
                            return this;
                        }

                        String[] parts = a[0].split("-");
                        var adt = readConfigAdapter(List.of(parts));

                        if (a.length > 1) {
                            String def = a[1];
                            return new Handler(adt, adt.convert(def));
                        }
                        return this;
                    }
            ));
        }
    }
}

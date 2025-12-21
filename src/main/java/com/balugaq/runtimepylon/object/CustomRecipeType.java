package com.balugaq.runtimepylon.object;

import com.balugaq.runtimepylon.config.BiGenericDeserializer;
import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.GenericDeserializer;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.data.MyArrayList;
import com.balugaq.runtimepylon.data.MyObject2ObjectOpenHashMap;
import com.balugaq.runtimepylon.data.MyObjectOpenHashSet;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.item.ItemTypeWrapper;
import io.github.pylonmc.pylon.core.recipe.ConfigurableRecipeType;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.gui.Gui;

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

    public static final Map<String, Handler> DEFAULT_CONFIG_READER = Map.of(
            "inputs", new Handler(GenericDeserializer.newDeserializer(MyArrayList.class).setDeserializer(Deserializer.RECIPE_INPUT_ITEM), new ArrayList<>()),
            "results", new Handler(GenericDeserializer.newDeserializer(MyArrayList.class).setDeserializer(Deserializer.FLUID_OR_ITEM), new ArrayList<>())
    );

    private final List<String> structure;
    private final ItemStackProvider provider;
    private final Map<String, Handler> configReader;

    public CustomRecipeType(final NamespacedKey key, List<String> structure, @Nullable ItemStackProvider guiProvider, @Nullable Map<String, Handler> configReader) {
        super(key);
        this.structure = structure;
        this.provider = guiProvider == null ? Pack.DEFAULT_GUI_PROVIDER : guiProvider;
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
            gui.addIngredient(c, (provider == null ? Pack.DEFAULT_GUI_PROVIDER : provider).display(c, recipe));
        }
        return gui.build();
    }

    protected CustomRecipe loadRecipe(final NamespacedKey key, final ConfigSection section) {
        Map<String, Object> other = new HashMap<>();
        for (Map.Entry<String, Handler> e : configReader.entrySet()) {
            try {
                other.put(e.getKey(), ReflectionUtil.invokeMethod(section, "get", e.getKey(), e.getValue().deserializer().toAdapter(), e.getValue().defaultValue()));
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
    public record Handler(Deserializer<?> deserializer, @Nullable Object defaultValue) implements Deserializer<Handler> {
        public Handler() {
            this(null, null);
        }

        private static Deserializer<?> readDeserializer(List<String> parts) {
            if (parts.isEmpty()) {
                return Deserializer.ANY; // fallback
            }

            String p = parts.getFirst();
            try {
                return ReflectionUtil.getStaticValue(Deserializer.class, p.toUpperCase(), Deserializer.class);
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
            }

            if (p.equalsIgnoreCase("list")) {
                return GenericDeserializer.newDeserializer(MyArrayList.class).setDeserializer(readDeserializer(parts.subList(1, parts.size())));
            }

            if (p.equalsIgnoreCase("set")) {
                return GenericDeserializer.newDeserializer(MyObjectOpenHashSet.class).setDeserializer(readDeserializer(parts.subList(1, parts.size())));
            }

            if (p.equalsIgnoreCase("map")) {
                return BiGenericDeserializer.newDeserializer(MyObject2ObjectOpenHashMap.class)
                        .setDeserializer(readDeserializer(List.of(parts.get(0))))
                        .setDeserializer2(readDeserializer(List.of(parts.get(1))));
            }

            if (p.equalsIgnoreCase("enum")) {
                if (parts.size() < 2) {
                    return Deserializer.ANY; // fallback
                }
                Class<?> clazz = findClass(parts.get(1));
                if (clazz == null || !clazz.isEnum()) {
                    return Deserializer.ANY; // fallback
                }

                return Deserializer.enumDeserializer((Class<? extends Enum>) clazz);
            }

            return Deserializer.ANY; // fallback
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
                        var adt = readDeserializer(List.of(parts));

                        if (a.length > 1) {
                            String def = a[1];
                            return new Handler(adt, adt.deserialize(def));
                        }
                        return this;
                    }
            ));
        }
    }
}

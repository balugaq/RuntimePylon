package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.config.pack.Author;
import com.balugaq.runtimepylon.config.pack.Contributor;
import com.balugaq.runtimepylon.config.pack.GitHubUpdateLink;
import com.balugaq.runtimepylon.config.pack.PackID;
import com.balugaq.runtimepylon.config.pack.PackVersion;
import com.balugaq.runtimepylon.config.pack.WebsiteLink;
import com.balugaq.runtimepylon.config.register.RegisterCondition;
import com.balugaq.runtimepylon.config.register.RegisterConditions;
import com.balugaq.runtimepylon.data.MyObject2ObjectOpenHashMap;
import com.balugaq.runtimepylon.exceptions.DeserializationException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.exceptions.UnknownEnumException;
import com.balugaq.runtimepylon.exceptions.UnknownFluidException;
import com.balugaq.runtimepylon.exceptions.UnknownFluidOrItemException;
import com.balugaq.runtimepylon.exceptions.UnknownItemException;
import com.balugaq.runtimepylon.exceptions.UnknownKeyedException;
import com.balugaq.runtimepylon.exceptions.UnknownMultiblockComponentException;
import com.balugaq.runtimepylon.exceptions.UnknownSaveditemException;
import com.balugaq.runtimepylon.object.CustomRecipeType;
import com.balugaq.runtimepylon.util.ClassUtil;
import com.balugaq.runtimepylon.util.MinecraftVersion;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.item.ItemTypeWrapper;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.logistics.LogisticSlotType;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.RandomizedSound;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.ApiStatus.Obsolete;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3i;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This interface is used to deserialize a config structure, instead of file structure For the example:
 * <p>
 * <code>
 * <pre>
 * &#064;NoArgsConstructor(force = true)
 * class Foo implements Deserializer<Foo> {}
 * </pre>
 * </code>
 * <p>
 * The class `Foo` must be annotated with {@code @lombok.NoArgsConstructor(force = true)} to make
 * {@link #newDeserializer(Class)} work.
 *
 * @param <T>
 *         the type of the object.
 *
 * @author balugaq
 * @see PackID
 */
@FunctionalInterface
@NullMarked
public interface Deserializer<T> {
    default Class<?> type() {
        var t = ClassUtil.getFirstGenericSuperclassType(this.getClass());
        if (t == null) return Object.class;
        return t;
    }

    ItemStackDeserializer ITEMSTACK = new ItemStackDeserializer();
    PylonFluidDeserializer PYLON_FLUID = new PylonFluidDeserializer();
    MultiblockComponentDeserializer MULTIBLOCK_COMPONENT = new MultiblockComponentDeserializer();
    Vector3iDeserializer VECTOR3I = new Vector3iDeserializer();
    RecipeChoiceDeserializer RECIPE_CHOICE = new RecipeChoiceDeserializer();
    KeyedDeserializer<TrimPattern> TRIM_PATTERN = KeyedDeserializer.of(RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN));
    BlockDataDeserializer BLOCK_DATA = new BlockDataDeserializer();
    RecipeInputItemDeserializer RECIPE_INPUT_ITEM = new RecipeInputItemDeserializer();
    RecipeInputFluidDeserializer RECIPE_INPUT_FLUID = new RecipeInputFluidDeserializer();
    FluidOrItemDeserializer FLUID_OR_ITEM = new FluidOrItemDeserializer();
    FluidMapDeserializer FLUID_MAP = new FluidMapDeserializer();
    Deserializer<Byte> BYTE = warp(ConfigAdapter.BYTE);
    Deserializer<Short> SHORT = warp(ConfigAdapter.SHORT);
    Deserializer<Integer> INT = warp(ConfigAdapter.INT);
    Deserializer<Long> LONG = warp(ConfigAdapter.LONG);
    Deserializer<Float> FLOAT = warp(ConfigAdapter.FLOAT);
    Deserializer<Double> DOUBLE = warp(ConfigAdapter.DOUBLE);
    Deserializer<Character> CHAR = warp(ConfigAdapter.CHAR);
    Deserializer<Boolean> BOOLEAN = warp(ConfigAdapter.BOOLEAN);
    Deserializer<Object> ANY = warp(ConfigAdapter.ANY);
    Deserializer<String> STRING = warp(ConfigAdapter.STRING);
    Deserializer<NamespacedKey> NAMESPACED_KEY = warp(ConfigAdapter.NAMESPACED_KEY);
    Deserializer<Material> MATERIAL = enumDeserializer(Material.class).forceUpperCase();
    Deserializer<Sound> SOUND = keyedDeserializer(Registry.SOUNDS);
    Deserializer<RandomizedSound> RANDOMIZED_SOUND = warp(ConfigAdapter.RANDOMIZED_SOUND);
    Deserializer<FluidTemperature> FLUID_TEMPERATURE = enumDeserializer(FluidTemperature.class).forceUpperCase();
    Deserializer<CraftingBookCategory> CRAFTING_BOOK_CATEGORY = Deserializer.enumDeserializer(CraftingBookCategory.class).forceUpperCase();
    Deserializer<CookingBookCategory> COOKING_BOOK_CATEGORY = Deserializer.enumDeserializer(CookingBookCategory.class).forceUpperCase();
    Deserializer<PackDesc> PACK_DESC = Deserializer.newDeserializer(PackDesc.class);
    Deserializer<PackID> PACK_ID = Deserializer.newDeserializer(PackID.class);
    Deserializer<Author> AUTHOR = Deserializer.newDeserializer(Author.class);
    Deserializer<Contributor> CONTRIBUTOR = Deserializer.newDeserializer(Contributor.class);
    Deserializer<GitHubUpdateLink> GITHUB_UPDATE_LINK = Deserializer.newDeserializer(GitHubUpdateLink.class);
    Deserializer<PackVersion> PACK_VERSION = Deserializer.newDeserializer(PackVersion.class);
    Deserializer<WebsiteLink> WEBSITE_LINK = Deserializer.newDeserializer(WebsiteLink.class);
    Deserializer<CustomRecipeType.Handler> HANDLER = Deserializer.newDeserializer(CustomRecipeType.Handler.class);
    Deserializer<PluginDesc> PLUGIN_DESC = Deserializer.newDeserializer(PluginDesc.class);
    Deserializer<RegisterCondition> REGISTER_CONDITION = Deserializer.newDeserializer(RegisterCondition.class);
    Deserializer<SaveditemDesc> SAVEDITEM_DESC = Deserializer.newDeserializer(SaveditemDesc.class);
    Deserializer<RegisterConditions> REGISTER_CONDITIONS = Deserializer.newDeserializer(RegisterConditions.class);
    Deserializer<MinecraftVersion> MINECRAFT_VERSION = Deserializer.newDeserializer(MinecraftVersion.class);
    Deserializer<LogisticSlotType> LOGISTIC_SLOT_TYPE = enumDeserializer(LogisticSlotType.class).forceUpperCase();

    static <E extends Enum<E>> EnumDeserializer<E> enumDeserializer(Class<E> clazz) {
        return EnumDeserializer.of(clazz);
    }

    static < K extends Keyed> KeyedDeserializer<K> keyedDeserializer(Registry<K> registry) {
        return KeyedDeserializer.of(registry);
    }

    static <T> Deserializer<T> warp(ConfigAdapter<T> adapter) {
        return () -> ConfigReader.list(Object.class, adapter::convert);
    }

    /**
     * Create an instance of the object. All the data in this object are invalid. It just for call
     * {@link #deserialize(Object)}.
     *
     * @return an instance of the object.
     *
     * @author balugaq
     * @see #deserialize(Object)
     */
    @Obsolete
    static <T extends Deserializer<T>> T newDeserializer(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new DeserializationException(clazz, e);
        }
    }

    default ConfigAdapter<T> toAdapter() {
        return new ConfigAdapter<T>() {
            @Override
            public Type getType() {
                return type();
            }

            @Override
            public T convert(final Object value) {
                return deserialize(value);
            }
        };
    }

    /**
     * Unserializes an object.
     *
     * @param o
     *         the object to deserialize, it may be {@link ConfigurationSection}, {@link ArrayList}, or primitive type.
     *
     * @return an instance of the object.
     *
     * @author balugaq
     * @see Deserializer#newDeserializer(Class)
     */
    @UnknownNullability
    default T deserialize(@Nullable Object o) throws DeserializationException {
        if (o == null) throw new MissingArgumentException(this.getClass());
        for (ConfigReader<?, T> reader : readers()) {
            if (reader.type().isInstance(o)) {
                try {
                    @SuppressWarnings("unchecked") T v = (T) ReflectionUtil.invokeMethod(reader, "read", o);
                    if (v != null) return v;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new DeserializationException(getClass(), e.getCause());
                }
            }
        }

        throw new DeserializationException(this.getClass() + " doesn't support [" + o.getClass() + "] " + o);
    }

    @Nullable
    default T deserializeOrNull(@Nullable Object o) {
        try {
            return deserialize(o);
        } catch (DeserializationException e) {
            return null;
        }
    }

    List<ConfigReader<?, T>> readers();

    /**
     * @author balugaq
     */
    @NullMarked
    record KeyedDeserializer<K extends Keyed>(Registry<K> registry) implements Deserializer<K> {
        public static <K extends Keyed> KeyedDeserializer<K> of(Registry<K> registry) {
            return new KeyedDeserializer<>(registry);
        }

        @Override
        public List<ConfigReader<?, K>> readers() {
            return ConfigReader.list(
                    String.class, s -> {
                        var key = NamespacedKey.fromString(s);
                        if (key == null) throw new UnknownKeyedException(registry, s);
                        return registry.get(key);
                    }
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class ItemStackDeserializer implements Deserializer<ItemStack> {
        public static final Map<String, String> FIELD_RENAME = Map.of(
                "grass", "short_grass", "short_grass", "grass",
                "scute", "turtle_scute", "turtle_scute", "scute",
                "chain", "iron_chain", "iron_chain", "chain"
        );

        @Override
        @SneakyThrows
        public @UnknownNullability ItemStack deserialize(@Nullable Object o) {
            if (o == null) throw new MissingArgumentException();
            for (ConfigReader<?, ItemStack> reader : readers()) {
                if (reader.type().isInstance(o)) {
                    try {
                        return (ItemStack) ReflectionUtil.invokeMethod(reader, "read", o);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw e.getCause();
                    }
                }
            }
            return null;
        }

        @Internal
        @SuppressWarnings("DuplicateCondition")
        private static ItemStack fromString(String s) throws DeserializationException {
            List<String> para = new ArrayList<>();
            if (s.contains("|")) {
                String[] split = s.split("\\|");
                for (String s2 : split) {
                    para.add(s2.trim());
                }
            } else {
                para.add(s.trim());
            }

            for (String s2 : para) {
                if (s2.startsWith("minecraft:") || !s2.contains(":")) {
                    // item: minecraft:diamond
                    // item: diamond
                    //
                    // minecraft item
                    String fixed;
                    if (s2.startsWith("minecraft:")) {
                        fixed = s2.substring(10);
                    } else {
                        fixed = s2;
                    }

                    Material material = Material.getMaterial(fixed.toUpperCase());
                    if (material == null) {
                        // try to rename field
                        String rename = FIELD_RENAME.get(fixed);
                        if (rename != null) {
                            material = Material.getMaterial(rename.toUpperCase());
                        }
                    }

                    if (material != null) {
                        return new ItemStack(material);
                    }

                    // item: example_item
                    // pylon item
                    Optional<PylonItemSchema> sch = PylonRegistry.ITEMS.stream().filter(schema -> schema.getKey().getKey().equals(fixed)).findFirst();
                    if (sch.isPresent()) {
                        return sch.get().getItemStack();
                    }
                } else if (s2.startsWith("saveditem")) {
                    // item: saveditem:mypack:foo
                    // also supports saveditem:mypack:blocks/bar
                    if (s2.contains(":")) {
                        // item: saveditem:mypack:foo
                        String[] split = s2.split(":");
                        String pack = split[1];
                        String fileName = split[2];
                        return PackManager.findSaveditem(
                                Deserializer.newDeserializer(PackDesc.class).deserialize(pack),
                                Deserializer.newDeserializer(SaveditemDesc.class).deserialize(fileName)
                        );
                    } else {
                        // item: saveditem:foo
                        // also supports saveditem:blocks/foo
                        // find from all packs
                        for (Pack pack : PackManager.getPacks()) {
                            try {
                                return PackManager.findSaveditem(
                                        Deserializer.newDeserializer(PackDesc.class).deserialize(pack),
                                        Deserializer.newDeserializer(SaveditemDesc.class).deserialize(s2)
                                );
                            } catch (UnknownSaveditemException ignored) {
                            }
                        }
                        throw new UnknownSaveditemException(s2);
                    }
                } else if (s2.contains(":")) {
                    // item: pylonbase:loupe
                    // get item from pylon registry
                    NamespacedKey k = NamespacedKey.fromString(s2);
                    if (k == null) continue;

                    PylonItemSchema schema = PylonRegistry.ITEMS.get(k);
                    if (schema != null) {
                        return schema.getItemStack();
                    }
                }
            }

            throw new UnknownItemException(s);
        }

        @Override
        public List<ConfigReader<?, ItemStack>> readers() {
            return ConfigReader.list(
                    String.class, ItemStackDeserializer::fromString,
                    Map.class, map -> {
                        if (map.size() == 1) {
                            var k = map.keySet().stream().findFirst().get();
                            if (k instanceof String key) {
                                var a = map.getOrDefault(key, 1);
                                if (a instanceof Integer i) {
                                    return fromString(key).asQuantity(i);
                                } else {
                                    return fromString(key);
                                }
                            } else {
                                return ITEMSTACK.deserialize(k);
                            }
                        }

                        throw new DeserializationException("Invalid item desc(map): " + map);
                    },
                    ConfigurationSection.class, section -> {
                        var keys = section.getKeys(false);
                        if (keys.size() == 1) {
                            String key = keys.stream().findFirst().get();
                            if (!key.equals("material")) {
                                return fromString(key).asQuantity(section.getInt(key, 1));
                            }
                        }

                        // for section, we have these fields for optional
                        // item:
                        //   material: minecraft:diamond // or heads: hash/base64/url
                        //   amount: 1-99
                        //   compounds... (// todo)

                        String s = section.getString("material");
                        if (s == null) throw new MissingArgumentException("material");
                        try {
                            ItemStack item = fromString(s).clone();
                            item.setAmount(section.getInt("amount", 1));
                            return item;
                        } catch (UnknownItemException e) {
                            try {
                                return ConfigAdapter.ITEM_STACK.convert(section);
                            } catch (IllegalArgumentException e2) {
                                e2.addSuppressed(e);
                                throw e2;
                            }
                        }
                    }
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class EnumDeserializer<E extends Enum<E>> implements Deserializer<E> {
        private final Class<E> clazz;
        private Function<String, String> preHandle = s -> s;

        public EnumDeserializer(Class<E> clazz) {
            this.clazz = clazz;
        }

        public static <E extends Enum<E>> EnumDeserializer<E> of(Class<E> clazz) {
            return new EnumDeserializer<>(clazz);
        }

        public EnumDeserializer<E> forceUpperCase() {
            return updatePreHandle(String::toUpperCase);
        }

        public EnumDeserializer<E> updatePreHandle(Function<String, String> preHandle) {
            this.preHandle = preHandle.andThen(preHandle);
            return this;
        }

        @Override
        public @UnknownNullability E deserialize(@Nullable Object o) {
            try (var ignore = StackFormatter.setPosition("Reading " + clazz.getSimpleName())) {
                return read(o);
            } catch (Throwable e) {
                if (e instanceof IllegalArgumentException e2) {
                    StackFormatter.handle(new UnknownEnumException(clazz, e2.getMessage()));
                } else {
                    StackFormatter.handle(e);
                }
                return null;
            }
        }

        @Override
        public @Nullable E deserializeOrNull(@Nullable Object o) {
            try {
                return read(o);
            } catch (Throwable e) {
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        @Nullable
        private E read(@Nullable Object o) throws Throwable {
            if (o == null) throw new MissingArgumentException();
            for (ConfigReader<?, E> reader : readers()) {
                if (reader.type().isInstance(o)) {
                    try {
                        return (E) ReflectionUtil.invokeMethod(reader, "read", o);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw e.getCause();
                    }
                }
            }
            return null;
        }

        @Override
        public List<ConfigReader<?, E>> readers() {
            return ConfigReader.list(
                    String.class, s -> Enum.valueOf(clazz, preHandle.apply(s))
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class PylonFluidDeserializer implements Deserializer<PylonFluid> {
        @Override
        public List<ConfigReader<?, PylonFluid>> readers() {
            return ConfigReader.list(
                    String.class, s -> {
                        if (s.contains(":")) {
                            NamespacedKey key = NamespacedKey.fromString(s);
                            if (key != null) {
                                var v = PylonRegistry.FLUIDS.get(key);
                                if (v == null) throw new UnknownFluidException(s);
                                return v;
                            }
                        }

                        var r = PylonRegistry.FLUIDS.getValues().stream().filter(v -> v.getKey().getKey().equals(s)).toList();
                        if (r.isEmpty()) throw new UnknownFluidException(s);
                        return r.getFirst();
                    }
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class MultiblockComponentDeserializer implements Deserializer<PylonSimpleMultiblock.MultiblockComponent> {
        @Override
        public List<ConfigReader<?, PylonSimpleMultiblock.MultiblockComponent>> readers() {
            return ConfigReader.list(
                    String.class, s -> {
                        List<PylonSimpleMultiblock.MultiblockComponent> list = new ArrayList<>();
                        if (s.contains("|")) {
                            for (var s2 : s.split("\\|")) {
                                list.add(Deserializer.MULTIBLOCK_COMPONENT.deserialize(s2.trim()));
                            }
                        } else {
                            if (s.startsWith("minecraft:")) {
                                if (s.contains("[")) {
                                    var mat = s.substring(10, s.indexOf("["));
                                    Material material = MATERIAL.deserialize(mat);
                                    var data = s.substring(s.indexOf("["));
                                    list.add(new PylonSimpleMultiblock.VanillaBlockdataMultiblockComponent(material.createBlockData(data)));
                                } else {
                                    var mat = s.substring(10);
                                    list.add(new PylonSimpleMultiblock.VanillaMultiblockComponent(MATERIAL.deserialize(mat)));
                                }
                            } else {
                                var key = NamespacedKey.fromString(s);
                                if (key == null) throw new UnknownMultiblockComponentException(s);
                                if (!PylonRegistry.BLOCKS.contains(key)) {
                                    throw new UnknownMultiblockComponentException(s);
                                }
                                list.add(new PylonSimpleMultiblock.PylonMultiblockComponent(key));
                            }
                        }

                        return new PylonSimpleMultiblock.MixedMultiblockComponent(list);
                    }
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class Vector3iDeserializer implements Deserializer<Vector3i> {
        @Override
        public List<ConfigReader<?, Vector3i>> readers() {
            return ConfigReader.list(
                   String.class, s -> {
                       String[] split = s.split(";");
                       if (split.length != 3) throw new IllegalArgumentException("Invalid vector3i format: " + s);
                       return new Vector3i(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                   }
           );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class RecipeChoiceDeserializer implements Deserializer<RecipeChoice.ExactChoice> {
        @Override
        public List<ConfigReader<?, RecipeChoice.ExactChoice>> readers() {
            return ConfigReader.list(
                    String.class, s -> {
                        if (s.contains("||")) {
                            List<ItemStack> list = new ArrayList<>();
                            String[] slice = s.split("\\|\\|");
                            for (var s2 : slice) {
                                list.addAll(RECIPE_CHOICE.deserialize(s2).getChoices());
                            }
                            return new RecipeChoice.ExactChoice(list);
                        } else {
                            if (!s.contains(":") && !s.contains("#") && Material.getMaterial(s) == null) {
                                // item: example_item
                                // pylon item
                                List<PylonItemSchema> schs = PylonRegistry.ITEMS.stream().filter(schema -> schema.getKey().getKey().equals(s)).toList();
                                if (!schs.isEmpty()) {
                                    return new RecipeChoice.ExactChoice(schs.stream().map(PylonItemSchema::getItemStack).toList());
                                }
                            }

                            var item = ITEMSTACK.deserializeOrNull(s);
                            if (item != null) {
                                return new RecipeChoice.ExactChoice(item);
                            }

                            // tag
                            return new RecipeChoice.ExactChoice(ConfigAdapter.ITEM_TAG.convert(s).getValues().stream().map(ItemTypeWrapper::createItemStack).toList());
                        }
                    },
                    ConfigurationSection.class, s -> new RecipeChoice.ExactChoice(ITEMSTACK.deserialize(s)),
                    Map.class, s -> new RecipeChoice.ExactChoice(ITEMSTACK.deserialize(s))
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class BlockDataDeserializer implements Deserializer<BlockData> {
        @Override
        public List<ConfigReader<?, BlockData>> readers() {
            return ConfigReader.list(
                    String.class, Bukkit::createBlockData
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class RecipeInputItemDeserializer implements Deserializer<RecipeInput.Item> {
        @Override
        public List<ConfigReader<?, RecipeInput.Item>> readers() {
            return ConfigReader.list(
                    String.class, this::proxy,
                    Map.class, this::proxy,
                    ConfigurationSection.class, this::proxy
            );
        }

        private RecipeInput.Item proxy(Object s) {
            return new RecipeInput.Item(1, RECIPE_CHOICE.deserialize(s).getChoices().toArray(new ItemStack[0]));
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class RecipeInputFluidDeserializer implements Deserializer<RecipeInput.Fluid> {
        @Override
        public List<ConfigReader<?, RecipeInput.Fluid>> readers() {
            return ConfigReader.list(
                    Map.Entry.class, e -> {
                        var fluid = PYLON_FLUID.deserialize(e.getKey());
                        double amount = Double.parseDouble(String.valueOf(e.getValue()));
                        return new RecipeInput.Fluid(amount, fluid);
                    },
                    Map.class, m -> {
                        if (m.size() == 1) {
                            var fluid = PYLON_FLUID.deserialize(m.keySet().stream().findFirst().get());
                            double amount = Double.parseDouble(String.valueOf(m.values().stream().findFirst().get()));
                            return new RecipeInput.Fluid(amount, fluid);
                        } else {
                            var fluid = PYLON_FLUID.deserialize(m.get("fluid"));
                            double amount = Double.parseDouble(String.valueOf(m.get("amount")));
                            return new RecipeInput.Fluid(amount, fluid);
                        }
                    },
                    ConfigurationSection.class, s -> RECIPE_INPUT_FLUID.deserialize(s.getKeys(false).stream().map(k -> Map.of(k, s.get(k))).flatMap(a -> a.entrySet().stream()).collect(Collectors.toMap(a -> a.getKey(), b -> b.getValue(), (a, b) -> a)))
            );
        }
    }

    /**
     * @author balugaq
     */
    @NullMarked
    class FluidOrItemDeserializer implements Deserializer<FluidOrItem> {
        @Override
        public List<ConfigReader<?, FluidOrItem>> readers() {
            return ConfigReader.list(
                    String.class, s -> {
                        try {
                            var r = RECIPE_INPUT_ITEM.deserialize(s);
                            if (r != null)
                                return FluidOrItem.of(r.getItems().stream().findFirst().get().createItemStack());
                        } catch (Exception ignored) {
                        }

                        try {
                            var r2 = PYLON_FLUID.deserialize(s);
                            if (r2 != null) return FluidOrItem.of(r2, 144);
                        } catch (Exception ignored) {
                        }

                        try {
                            var r3 = ITEMSTACK.deserialize(s);
                            if (r3 != null) return FluidOrItem.of(r3);
                        } catch (Exception ignored) {
                        }

                        throw new UnknownFluidOrItemException(s);
                    },
                    Map.class, m -> {
                        try {
                            var r = RECIPE_INPUT_ITEM.deserialize(m);
                            if (r != null)
                                return FluidOrItem.of(r.getItems().stream().findFirst().get().createItemStack());
                        } catch (Exception ignored) {
                        }

                        try {
                            var r2 = RECIPE_INPUT_FLUID.deserialize(m);
                            if (r2 != null)
                                return FluidOrItem.of(r2.fluids().stream().findFirst().get(), r2.amountMillibuckets());
                        } catch (Exception ignored) {
                        }

                        try {
                            var r3 = ITEMSTACK.deserialize(m);
                            if (r3 != null) return FluidOrItem.of(r3);
                        } catch (Exception ignored) {
                        }

                        throw new UnknownFluidOrItemException(m.toString());
                    },
                    ConfigurationSection.class, c -> {
                        try {
                            var r = RECIPE_INPUT_ITEM.deserialize(c);
                            if (r != null)
                                return FluidOrItem.of(r.getItems().stream().findFirst().get().createItemStack());
                        } catch (Exception ignored) {
                        }

                        try {
                            var r2 = RECIPE_INPUT_FLUID.deserialize(c);
                            if (r2 != null)
                                return FluidOrItem.of(r2.fluids().stream().findFirst().get(), r2.amountMillibuckets());
                        } catch (Exception ignored) {
                        }

                        try {
                            var r3 = ITEMSTACK.deserializeOrNull(c);
                            if (r3 != null) return FluidOrItem.of(r3);
                        } catch (Exception ignored) {
                        }

                        throw new UnknownFluidOrItemException(c.toString());
                    }
            );
        }
    }

    class FluidMapDeserializer extends MyObject2ObjectOpenHashMap<PylonFluid, Double> {
        public FluidMapDeserializer() {
            setGenericType(PylonFluid.class);
            setGenericType2(Double.class);
            setDeserializer(PYLON_FLUID);
            setDeserializer2(() -> ConfigReader.list(String.class, Double::parseDouble, Double.class, s -> s, Integer.class, s -> (double)s));
            setAdvancer(t -> t);
            setAdvancer2(t -> t);
        }
    }
}

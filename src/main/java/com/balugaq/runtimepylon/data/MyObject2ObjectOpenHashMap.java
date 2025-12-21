package com.balugaq.runtimepylon.data;

import com.balugaq.runtimepylon.config.Advancer;
import com.balugaq.runtimepylon.config.BiGenericDeserializer;
import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.StackFormatter;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @param <T1>
 *         the type of the key elements in this map
 * @param <T2>
 *         the type of the value elements in this map
 *
 * @author balugaq
 */
@EqualsAndHashCode(callSuper = true)
@NullMarked
public class MyObject2ObjectOpenHashMap<T1, T2> extends Object2ObjectOpenHashMap<T1, T2> implements BiGenericDeserializer<MyObject2ObjectOpenHashMap<T1, T2>, T1, T2> {
    @Getter
    @Nullable
    private Class<T1> genericType;

    @Getter
    @Nullable
    private Class<T2> genericType2;

    @Getter
    private @UnknownNullability Advancer<Deserializer<T1>> advancer;

    @Getter
    private @UnknownNullability Advancer<Deserializer<T2>> advancer2;

    @UnknownNullability
    private Deserializer<T1> deserializer;

    @UnknownNullability
    private Deserializer<T2> deserializer2;

    @Override
    public MyObject2ObjectOpenHashMap<T1, T2> setAdvancer(final Advancer<Deserializer<T1>> advancer) {
        this.advancer = advancer;
        return this;
    }

    @Override
    public MyObject2ObjectOpenHashMap<T1, T2> setGenericType(Class<T1> clazz) {
        this.genericType = clazz;
        return this;
    }

    @Override
    public MyObject2ObjectOpenHashMap<T1, T2> setDeserializer(final Deserializer<T1> deserializer) {
        this.deserializer = deserializer;
        return this;
    }

    @Override
    public MyObject2ObjectOpenHashMap<T1, T2> setAdvancer2(final Advancer<Deserializer<T2>> advancer) {
        this.advancer2 = advancer;
        return this;
    }

    @Override
    public MyObject2ObjectOpenHashMap<T1, T2> setGenericType2(Class<T2> clazz) {
        this.genericType2 = clazz;
        return this;
    }

    @Override
    public MyObject2ObjectOpenHashMap<T1, T2> setDeserializer2(final Deserializer<T2> deserializer) {
        this.deserializer2 = deserializer;
        return this;
    }

    @Override
    public List<ConfigReader<?, MyObject2ObjectOpenHashMap<T1, T2>>> readers() {
        return ConfigReader.list(
                Map.class, map -> {
                    var serializer = advancer.advance(getDeserializer());
                    var serializer2 = advancer2.advance(getDeserializer2());
                    MyObject2ObjectOpenHashMap<T1, T2> res = new MyObject2ObjectOpenHashMap<>();
                    for (Map.Entry<Object, Object> e : (Set<Map.Entry<Object, Object>>) map.entrySet()) {
                        try (var ignored = StackFormatter.setPosition("Reading Map<" + getDeserializer().type() + ", " + getDeserializer2().type() + ">")) {
                            res.put(serializer.deserialize(e.getKey()), serializer2.deserialize(e.getValue()));
                        } catch (Exception ex) {
                            StackFormatter.handle(ex);
                        }
                    }
                    return res;
                },
                ConfigurationSection.class, section -> {
                    var serializer = advancer.advance(getDeserializer());
                    var serializer2 = advancer2.advance(getDeserializer2());
                    MyObject2ObjectOpenHashMap<T1, T2> res = new MyObject2ObjectOpenHashMap<>();
                    for (String key : section.getKeys(false)) {
                        try (var ignored = StackFormatter.setPosition("Reading Map<" + getDeserializer().type() + ", " + getDeserializer2().type() + ">")) {
                            res.put(serializer.deserialize(key), serializer2.deserialize(section.get(key)));
                        } catch (Exception ex) {
                            StackFormatter.handle(ex);
                        }
                    }
                    return res;
                }
        );
    }

    @Override
    public Deserializer<T1> getDeserializer() {
        if (deserializer != null) return deserializer;

        final Deserializer<T1> t = BiGenericDeserializer.super.getDeserializer();
        if (t == null) throw new UnsupportedOperationException("Not deserializable and no deserializer provided");
        return t;
    }

    @Override
    public Deserializer<T2> getDeserializer2() {
        if (deserializer2 != null) return deserializer2;

        final Deserializer<T2> t = BiGenericDeserializer.super.getDeserializer2();
        if (t == null) throw new UnsupportedOperationException("Not deserializable and no deserializer provided");
        return t;
    }
}

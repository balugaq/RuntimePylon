package com.balugaq.runtimepylon.data;

import com.balugaq.runtimepylon.config.Advancer;
import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.GenericDeserializer;
import com.balugaq.runtimepylon.config.StackFormatter;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @param <T>
 *         the type of the elements in this set
 *
 * @author balugaq
 */
@EqualsAndHashCode(callSuper = true)
@NullMarked
public class MyObjectOpenHashSet<T> extends ObjectOpenHashSet<T> implements GenericDeserializer<MyObjectOpenHashSet<T>, T> {
    @Getter
    @Nullable
    private Class<T> genericType;

    @Getter
    private @UnknownNullability Advancer<Deserializer<T>> advancer;

    @UnknownNullability
    private Deserializer<T> deserializer;

    @Override
    public MyObjectOpenHashSet<T> setAdvancer(Advancer<Deserializer<T>> advancer) {
        this.advancer = advancer;
        return this;
    }

    @Override
    public MyObjectOpenHashSet<T> setGenericType(Class<T> clazz) {
        this.genericType = clazz;
        return this;
    }

    @Override
    public List<ConfigReader<?, MyObjectOpenHashSet<T>>> readers() {
        return ConfigReader.list(
                List.class, lst -> {
                    Deserializer<T> serializer = advancer.advance(getDeserializer());
                    MyObjectOpenHashSet<T> res = new MyObjectOpenHashSet<>();
                    for (Object object : lst) {
                        try (var ignored = StackFormatter.setPosition("Reading Set<" + getDeserializer().type() + ">")) {
                            res.add(serializer.deserialize(object));
                        } catch (Exception e) {
                            StackFormatter.handle(e);
                        }
                    }
                    return res;
                },
                Object.class, s -> {
                    Deserializer<T> serializer = advancer.advance(getDeserializer());
                    MyObjectOpenHashSet<T> res = new MyObjectOpenHashSet<>();
                    try (var ignored = StackFormatter.setPosition("Reading Set<" + getDeserializer().type() + ">")) {
                        res.add(serializer.deserialize(s));
                    } catch (Exception e) {
                        StackFormatter.handle(e);
                    }
                    return res;
                }
        );
    }

    @Override
    public Deserializer<T> getDeserializer() {
        if (deserializer != null) return deserializer;

        final Deserializer<T> t = GenericDeserializer.super.getDeserializer();
        if (t == null) throw new UnsupportedOperationException("Not deserializable and no deserializer provided");
        return t;
    }

    @Override
    public MyObjectOpenHashSet<T> setDeserializer(final Deserializer<T> deserializer) {
        this.deserializer = deserializer;
        return this;
    }
}

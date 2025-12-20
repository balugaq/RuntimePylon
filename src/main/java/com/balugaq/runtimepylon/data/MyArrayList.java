package com.balugaq.runtimepylon.data;

import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.GenericDeserializer;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.StackFormatter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <T>
 *         the type of the elements in this list
 *
 * @author balugaq
 */
@EqualsAndHashCode(callSuper = true)
@NullMarked
public class MyArrayList<T> extends ArrayList<T> implements GenericDeserializer<MyArrayList<T>, T> {
    @Getter
    @UnknownNullability
    private Class<T> genericType;

    @Getter
    private Pack.@UnknownNullability Advancer<Deserializer<T>> advancer;

    @UnknownNullability
    private Deserializer<T> deserializer;

    @Override
    public MyArrayList<T> setAdvancer(Pack.Advancer<Deserializer<T>> advancer) {
        this.advancer = advancer;
        return this;
    }

    @Override
    public MyArrayList<T> setGenericType(Class<T> clazz) {
        this.genericType = clazz;
        return this;
    }

    @Override
    public List<ConfigReader<?, MyArrayList<T>>> readers() {
        return ConfigReader.list(
                List.class, lst -> {
                    Deserializer<T> serializer = advancer.advance(getDeserializer());
                    MyArrayList<T> res = new MyArrayList<>();
                    for (Object object : lst) {
                        try (var ignored = StackFormatter.setPosition("Reading List<" + getGenericType().getSimpleName() + ">")) {
                            res.add(serializer.deserialize(object));
                        } catch (Exception e) {
                            StackFormatter.handle(e);
                        }
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
    public MyArrayList<T> setDeserializer(final Deserializer<T> deserializer) {
        this.deserializer = deserializer;
        return this;
    }
}

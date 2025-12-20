package com.balugaq.runtimepylon.data;

import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.GenericDeserializer;
import com.balugaq.runtimepylon.config.Pack;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;

/**
 * @author balugaq
 */
@NullMarked
@RequiredArgsConstructor
public @Data class WeightedElement<T> implements Deserializer<WeightedElement<T>>, GenericDeserializer<WeightedElement<T>, T> {
    private final T element;
    private final float weight;

    @UnknownNullability
    private Class<T> genericType;

    @Getter
    private Pack.@UnknownNullability Advancer<Deserializer<T>> advancer;

    @UnknownNullability
    private Deserializer<T> deserializer;

    @Override
    public List<ConfigReader<?, WeightedElement<T>>> readers() {
        return ConfigReader.list(
                Map.class, map -> {
                    var v = map.get("weight");
                    if (v instanceof Float f) {
                        return new WeightedElement<>(
                                advancer.advance(getDeserializer()).deserialize(map.get("value")),
                                f
                        );
                    }

                    throw new IllegalArgumentException("weight: " + v);
                }
        );
    }

    @Override
    public WeightedElement<T> setAdvancer(final Pack.Advancer<Deserializer<T>> advancer) {
        this.advancer = advancer;
        return this;
    }

    @Override
    public Class<T> getGenericType() {
        return (Class<T>) element.getClass();
    }

    @Override
    public WeightedElement<T> setGenericType(final Class<T> clazz) {
        this.genericType = clazz;
        return this;
    }

    @Override
    public WeightedElement<T> setDeserializer(final Deserializer<T> deserializer) {
        this.deserializer = deserializer;
        return this;
    }

    @Override
    public Deserializer<T> getDeserializer() {
        if (deserializer != null) return deserializer;

        final Deserializer<T> t = GenericDeserializer.super.getDeserializer();
        if (t == null) throw new UnsupportedOperationException("Not deserializable and no deserializer provided");
        return t;
    }
}

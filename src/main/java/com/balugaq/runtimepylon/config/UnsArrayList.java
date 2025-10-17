package com.balugaq.runtimepylon.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <T> the type of the elements in this list
 * @author balugaq
 */
@EqualsAndHashCode(callSuper = true)
public class UnsArrayList<T extends Deserializer<T>> extends ArrayList<T> implements GenericDeserializer<@NotNull UnsArrayList<T>, @NotNull T> {
    @Getter
    @UnknownNullability private Class<T> genericType;

    @Getter
    private Pack.@UnknownNullability Advancer<T> advancer;

    @NotNull
    @Override
    public UnsArrayList<T> setGenericType(@NotNull Class<T> clazz) {
        this.genericType = clazz;
        return this;
    }

    @Override
    public @NotNull UnsArrayList<T> setAdvancer(Pack.@NotNull Advancer<T> advancer) {
        this.advancer = advancer;
        return this;
    }

    @NotNull
    @Override
    public List<ConfigReader<?, UnsArrayList<T>>> readers() {
        return List.of(
                ConfigReader.of(ArrayList.class, lst -> {
                    var serializer = advancer.advance(Deserializer.newDeserializer(getGenericType()));
                    UnsArrayList<T> res = new UnsArrayList<>();
                    for (Object object : lst) {
                        try {
                            res.add(serializer.deserialize(object));
                        } catch (Exception e) {
                            StackWalker.handle(e);
                        }
                    }
                    return res;
                })
        );
    }
}

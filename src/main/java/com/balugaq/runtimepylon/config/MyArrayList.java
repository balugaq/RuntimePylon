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
public class MyArrayList<T extends Deserializer<T>> extends ArrayList<T> implements GenericDeserializer<@NotNull MyArrayList<T>, @NotNull T> {
    @Getter
    @UnknownNullability private Class<T> genericType;

    @Getter
    private Pack.@UnknownNullability Advancer<T> advancer;

    @Getter
    private boolean skipFail;

    @NotNull
    @Override
    public MyArrayList<T> setGenericType(@NotNull Class<T> clazz) {
        this.genericType = clazz;
        return this;
    }

    @Override
    public @NotNull MyArrayList<T> setAdvancer(Pack.@NotNull Advancer<T> advancer) {
        this.advancer = advancer;
        return this;
    }

    @NotNull
    @Override
    public List<ConfigReader<?, MyArrayList<T>>> readers() {
        return List.of(
                ConfigReader.of(List.class, lst -> {
                    var serializer = advancer.advance(Deserializer.newDeserializer(getGenericType()));
                    MyArrayList<T> res = new MyArrayList<>();
                    for (Object object : lst) {
                        try (var ignored = StackWalker.setPosition("Reading List<" + getGenericType().getSimpleName() + ">")) {
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

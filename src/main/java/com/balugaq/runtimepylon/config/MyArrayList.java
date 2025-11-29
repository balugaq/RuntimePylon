package com.balugaq.runtimepylon.config;

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
public class MyArrayList<T extends Deserializer<T>> extends ArrayList<T> implements GenericDeserializer<MyArrayList<T>, T> {
    @Getter
    @UnknownNullability
    private Class<T> genericType;

    @Getter
    private Pack.@UnknownNullability Advancer<T> advancer;

    @Getter
    private boolean skipFail;

    @Override
    public MyArrayList<T> setAdvancer(Pack.Advancer<T> advancer) {
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
        return List.of(ConfigReader.of(
                List.class, lst -> {
                    var serializer = advancer.advance(Deserializer.newDeserializer(getGenericType()));
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
        ));
    }
}

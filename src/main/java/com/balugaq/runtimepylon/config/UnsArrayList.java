package com.balugaq.runtimepylon.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class UnsArrayList<T extends Deserializer<T>> extends ArrayList<T> implements GenericDeserializer<@NotNull UnsArrayList<T>, @NotNull T> {
    @Getter
    private Class<T> genericType;

    @NotNull
    @Override
    public UnsArrayList<T> setGenericType(@NotNull Class<T> clazz) {
        this.genericType = clazz;
        return this;
    }

    @NotNull
    @Override
    public List<ConfigReader<?, UnsArrayList<T>>> readers() {
        return List.of(
                ConfigReader.of(ArrayList.class, lst -> {
                    var serializer = Deserializer.newDeserializer(getGenericType());
                    UnsArrayList<T> res = new UnsArrayList<>();
                    for (Object object : lst) {
                        res.add(serializer.deserialize(object));
                    }
                    return res;
                })
        );
    }
}

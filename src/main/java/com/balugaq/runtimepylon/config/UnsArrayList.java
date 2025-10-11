package com.balugaq.runtimepylon.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NullMarked
public class UnsArrayList<T extends Deserializer<T>> extends ArrayList<T> implements GenericDeserializer<UnsArrayList<T>, T> {
    @Getter
    private Class<T> genericType;

    @Override
    public UnsArrayList<T> setGenericType(Class<T> clazz) {
        this.genericType = clazz;
        return this;
    }

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

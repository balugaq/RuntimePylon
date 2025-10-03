package com.balugaq.runtimepylon.config;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class UnsArrayList<T extends Deserializable<T>> extends ArrayList<T> implements Deserializable<UnsArrayList<T>>, GenericObject<UnsArrayList<T>, T> {
    @Override
    public List<ConfigReader<?, UnsArrayList<T>>> readers() {
        return List.of(
                ConfigReader.of(ArrayList.class, lst -> {
                    var serializer = Deserializable.newDeserializer(getGenericType());
                    UnsArrayList<T> res = new UnsArrayList<>();
                    for (Object object : lst) {
                        res.add(serializer.deserialize(object));
                    }
                    return res;
                })
        );
    }

    @Setter
    @Getter
    private Class<T> genericType;
}

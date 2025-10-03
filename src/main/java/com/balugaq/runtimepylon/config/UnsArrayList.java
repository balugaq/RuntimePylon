package com.balugaq.runtimepylon.config;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class UnsArrayList<T extends Unserializable<T>> extends ArrayList<T> implements Unserializable<UnsArrayList<T>>, GenericObject<T> {
    @Override
    public List<Reader<?, UnsArrayList<T>>> readers() {
        return List.of(
                Reader.of(ArrayList.class, lst -> {
                    var serializer = Unserializable.newSerializer(getGenericType());
                    UnsArrayList<T> res = new UnsArrayList<>();
                    for (int i = 0; i < lst.size(); i++) {
                        var o = lst.get(i);
                        @Nullable var v = serializer.unserialize(o);
                        if (v == null) {
                            analyzeWithStackTrace("Unable to serialize #" + i + " object");
                        }
                    }
                    return res;
                })
        );
    }

    @Setter
    @Getter
    private Class<T> genericType;
}

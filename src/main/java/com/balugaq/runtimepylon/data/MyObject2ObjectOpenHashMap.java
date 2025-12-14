package com.balugaq.runtimepylon.data;

import com.balugaq.runtimepylon.config.BiGenericDeserializer;
import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.GenericDeserializer;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.StackFormatter;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @param <T1>
 *         the type of the key elements in this map
 * @param <T2>
 *         the type of the value elements in this map
 *
 * @author balugaq
 */
@EqualsAndHashCode(callSuper = true)
@NullMarked
public class MyObject2ObjectOpenHashMap<T1 extends Deserializer<T1>, T2 extends Deserializer<T2>> extends Object2ObjectOpenHashMap<T1, T2> implements BiGenericDeserializer<MyObject2ObjectOpenHashMap<T1, T2>, T1, T2> {
    @Getter
    @UnknownNullability
    private Class<T1> genericType;

    @Getter
    @UnknownNullability
    private Class<T2> genericType2;

    @Getter
    private Pack.@UnknownNullability Advancer<T1> advancer;

    @Getter
    private Pack.@UnknownNullability Advancer<T2> advancer2;

    @Getter
    private boolean skipFail;

    @Override
    public MyObject2ObjectOpenHashMap<T1, T2> setAdvancer(Pack.Advancer<T1> advancer) {
        this.advancer = advancer;
        return this;
    }

    @Override
    public MyObject2ObjectOpenHashMap<T1, T2> setAdvancer2(Pack.Advancer<T2> advancer) {
        this.advancer2 = advancer;
        return this;
    }

    @Override
    public MyObject2ObjectOpenHashMap<T1, T2> setGenericType(Class<T1> clazz) {
        this.genericType = clazz;
        return this;
    }

    @Override
    public MyObject2ObjectOpenHashMap<T1, T2> setGenericType2(Class<T2> clazz) {
        this.genericType2 = clazz;
        return this;
    }

    @Override
    public List<ConfigReader<?, MyObject2ObjectOpenHashMap<T1, T2>>> readers() {
        return ConfigReader.list(
                Map.class, map -> {
                    var serializer = advancer.advance(Deserializer.newDeserializer(getGenericType()));
                    var serializer2 = advancer2.advance(Deserializer.newDeserializer(getGenericType2()));
                    MyObject2ObjectOpenHashMap<T1, T2> res = new MyObject2ObjectOpenHashMap<>();
                    for (Map.Entry<Object, Object> e : (Set<Map.Entry<Object, Object>>) map.entrySet()) {
                        try (var ignored = StackFormatter.setPosition("Reading Map<" + getGenericType().getSimpleName() + ", " + getGenericType2().getSimpleName() + ">")) {
                            res.put(serializer.deserialize(e.getKey()), serializer2.deserialize(e.getValue()));
                        } catch (Exception ex) {
                            StackFormatter.handle(ex);
                        }
                    }
                    return res;
                }
        );
    }
}

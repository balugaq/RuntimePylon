package com.balugaq.runtimepylon.data;

import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.GenericDeserializer;
import com.balugaq.runtimepylon.config.Pack;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;

/**
 * @author balugaq
 */
@NullMarked
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public @Data class WeightedElement implements Deserializer<WeightedElement> {
    private final ItemStack element;
    private final float weight;

    @Override
    public List<ConfigReader<?, WeightedElement>> readers() {
        return ConfigReader.list(
                Map.class, map -> {
                    var v = map.get("weight");
                    if (v instanceof Number f) {
                        return new WeightedElement(
                                Deserializer.ITEMSTACK.deserialize(map.get("value")),
                                f.floatValue()
                        );
                    }

                    throw new IllegalArgumentException("weight: " + v);
                },
                String.class, s ->
                    new WeightedElement(
                            Deserializer.ITEMSTACK.deserialize(s),
                            1f
                    )
        );
    }
}

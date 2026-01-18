package com.balugaq.pc.data;

import com.balugaq.pc.config.ConfigReader;
import com.balugaq.pc.config.Deserializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
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

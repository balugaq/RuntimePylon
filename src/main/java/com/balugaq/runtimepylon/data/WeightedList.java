package com.balugaq.runtimepylon.data;

import io.github.pylonmc.pylon.core.util.WeightedSet;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class WeightedList<T> extends MyArrayList<WeightedElement<T>> {
    public WeightedSet<T> toWeightedSet() {
        WeightedSet<T> set = new WeightedSet<>();
        for (WeightedElement<T> element : this) {
            set.add(new WeightedSet.Element<>(element.getElement(), element.getWeight()));
        }
        return set;
    }
}

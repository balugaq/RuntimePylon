package com.balugaq.pc.config;

import io.github.pylonmc.pylon.core.logistics.LogisticGroupType;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public record LogisticBlockData(NamespacedKey key, @Unmodifiable List<SingletonLogisticBlockData> data) implements Iterable<SingletonLogisticBlockData> {
    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<SingletonLogisticBlockData> iterator() {
        return data.iterator();
    }

    public List<Character> inputInventories() {
        List<Character> chars = new ArrayList<>();
        for (var e : data) {
            if (e.slotType() == LogisticGroupType.INPUT || e.slotType() == LogisticGroupType.BOTH) {
                chars.add(e.invSlotChar());
            }
        }
        return chars;
    }

    public List<Character> outputInventories() {
        List<Character> chars = new ArrayList<>();
        for (var e : data) {
            if (e.slotType() == LogisticGroupType.OUTPUT || e.slotType() == LogisticGroupType.BOTH) {
                chars.add(e.invSlotChar());
            }
        }
        return chars;
    }
}

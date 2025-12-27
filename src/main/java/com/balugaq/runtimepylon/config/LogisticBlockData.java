package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.GlobalVars;
import io.github.pylonmc.pylon.core.logistics.LogisticSlotType;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.inventory.VirtualInventory;

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
            if (e.slotType() == LogisticSlotType.INPUT || e.slotType() == LogisticSlotType.BOTH) {
                chars.add(e.invSlotChar());
            }
        }
        return chars;
    }

    public List<Character> outputInventories() {
        List<Character> chars = new ArrayList<>();
        for (var e : data) {
            if (e.slotType() == LogisticSlotType.OUTPUT || e.slotType() == LogisticSlotType.BOTH) {
                chars.add(e.invSlotChar());
            }
        }
        return chars;
    }
}

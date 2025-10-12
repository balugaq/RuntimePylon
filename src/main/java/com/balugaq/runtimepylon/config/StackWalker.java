package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.util.Debug;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.Range;

public class StackWalker implements AutoCloseable {
    private static final StackWalker inst = new StackWalker();
    private static final Int2ObjectOpenHashMap<String> positions = new Int2ObjectOpenHashMap<>();

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static void handle(Exception e) {
        Debug.warn(e.getClass().getSimpleName() + ": " + e.getMessage());
        for (int i = 1; i <= getCurrent(); i++) {
            if (i == 1) {
                Debug.warn("When " + positions.get(i));
                continue;
            }

            Debug.warn("  ".repeat(i - 1) + "\u2514When " + positions.get(i));
        }
    }

    public static int getCurrent() {
        return positions.size();
    }

    public static StackWalker setPosition(@Range(from = 1, to = Integer.MAX_VALUE) int level, String position) {
        if (level < getCurrent()) {
            int c = getCurrent();
            for (int i = level + 1; i < c; i++) {
                positions.remove(i);
            }
        }

        positions.put(level, position);
        return inst;
    }

    @CanIgnoreReturnValue
    public static StackWalker setPosition(String position) {
        return setPosition(getCurrent() + 1, position);
    }

    @CanIgnoreReturnValue
    public static StackWalker destroy() {
        positions.remove(getCurrent());
        return inst;
    }

    public static void runWith(String position, Runnable runnable) {
        setPosition(position);
        runnable.run();
        destroy();
    }

    @Override
    public void close() {
        destroy();
    }
}

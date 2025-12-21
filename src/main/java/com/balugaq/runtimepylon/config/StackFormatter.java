package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.util.Debug;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class StackFormatter implements AutoCloseable {
    private static final StackFormatter inst = new StackFormatter();
    @Getter
    private static final Int2ObjectOpenHashMap<String> backup = new Int2ObjectOpenHashMap<>();
    @Getter
    private static final Int2ObjectOpenHashMap<String> positions = new Int2ObjectOpenHashMap<>();

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static void handle(Throwable e) {
        Throwable t = e;
        int k = 0;
        while (t instanceof RuntimeException && k++ < 10) {
            if (t.getCause() != null) {
                t = t.getCause();
            }
        }

        Debug.warn(t.getClass().getSimpleName() + ": " + t.getMessage());

        for (int i = 1; i <= backup.size(); i++)
            Debug.warn("  ".repeat(i - 1) + "\u2514When " + backup.get(i));

        if (RuntimePylon.getConfigManager().isDebug())
            e.printStackTrace();

        Debug.warn("-".repeat(40));
        if (RuntimePylon.getConfigManager().isDebug())
            Thread.dumpStack();
    }

    public static void run(String position, Runnable runnable) {
        setPosition(position);
        runnable.run();
        destroy();
    }

    @CanIgnoreReturnValue
    public static StackFormatter setPosition(String position) {
        return setPosition(positions.size() + 1, position);
    }

    @CanIgnoreReturnValue
    public static StackFormatter destroy() {
        syncBackup();
        positions.remove(positions.size());
        return inst;
    }

    public static StackFormatter setPosition(@Range(from = 1, to = Integer.MAX_VALUE) int level, String position) {
        syncBackup();
        if (level < positions.size()) {
            int c = positions.size();
            for (int i = level + 1; i < c; i++) {
                positions.remove(i);
            }
        }

        positions.put(level, position);
        return inst;
    }

    private static void syncBackup() {
        backup.clear();
        backup.putAll(positions);
    }

    public static StackFormatter getInstance() {
        return inst;
    }

    @Override
    public void close() {
        destroy();
    }
}

package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.exceptions.DeserializationException;
import com.balugaq.runtimepylon.util.Debug;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import org.jetbrains.annotations.Range;

public class StackWalker implements AutoCloseable {
    private static final StackWalker inst = new StackWalker();
    @Getter
    private static final Int2ObjectOpenHashMap<String> backup = new Int2ObjectOpenHashMap<>();
    @Getter
    private static final Int2ObjectOpenHashMap<String> positions = new Int2ObjectOpenHashMap<>();

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static void handle(Throwable e) {
        Throwable t = e;
        if (e instanceof DeserializationException && e.getCause() != null) {
            t = e.getCause();
        }
        Debug.warn(t.getClass().getSimpleName() + ": " + t.getMessage());

        for (int i = 1; i <= backup.size(); i++)
            Debug.warn("  ".repeat(i - 1) + "\u2514When " + backup.get(i));

        if (RuntimePylon.getConfigManager().isDebug())
            e.printStackTrace();

        Debug.warn("-".repeat(40));
    }

    public static StackWalker setPosition(@Range(from = 1, to = Integer.MAX_VALUE) int level, String position) {
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

    @CanIgnoreReturnValue
    public static StackWalker setPosition(String position) {
        return setPosition(positions.size() + 1, position);
    }

    private static void syncBackup() {
        backup.clear();
        backup.putAll(positions);
    }

    @CanIgnoreReturnValue
    public static StackWalker destroy() {
        syncBackup();
        positions.remove(positions.size());
        return inst;
    }

    public static void run(String position, Runnable runnable) {
        setPosition(position);
        runnable.run();
        destroy();
    }

    @Override
    public void close() {
        destroy();
    }

    public static StackWalker getInstance() {
        return inst;
    }
}

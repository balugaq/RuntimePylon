package com.balugaq.runtimepylon.config;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@NullMarked
public sealed interface Reader<Input, Result> permits Reader.ReaderImpl {
    static <Input, Result> Reader<Input, Result> of(Class<Input> clazz, Function<Input, @Nullable Result> function) {
        return new ReaderImpl<>(clazz, function);
    }

    Class<Input> type();

    /**
     * Read an object into Result
     * @param o object to read
     * @return Result
     * @see Unserializable#unserialize(Object)
     */
    @Nullable
    Result read(Input o);

    final class ReaderImpl<Input, Result> implements Reader<Input, Result> {
        private final Class<Input> clazz;
        private final Function<Input, Result> function;
        public ReaderImpl(Class<Input> clazz, Function<Input, @Nullable Result> function) {
            this.clazz = clazz;
            this.function = function;
        }

        @Override
        public Class<Input> type() {
            return clazz;
        }

        @SuppressWarnings("DataFlowIssue")
        @Override
        @Nullable
        public Result read(Input o) throws UnserializableException {
            return function.apply(o);
        }
    }
}

package com.balugaq.pc.config;

import com.balugaq.pc.exceptions.DeserializationException;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Function;

/**
 * @author balugaq
 */
@NullMarked
public sealed interface ConfigReader<Input, Result> permits ConfigReader.ConfigReaderImpl {
    static <Input, Result> List<ConfigReader<?, Result>> list(Class<Input> clazz, Function<Input, @Nullable Result> function) {
        return List.of(of(clazz, function));
    }

    static <Input, Input2, Result> List<ConfigReader<?, Result>> list(Class<Input> clazz1, Function<Input, @Nullable Result> function1,
                                                                  Class<Input2> clazz2, Function<Input2, @Nullable Result> function2) {
        return List.of(of(clazz1, function1), of(clazz2, function2));
    }

    static <Input, Input2, Input3, Result> List<ConfigReader<?, Result>> list(Class<Input> clazz1, Function<Input, @Nullable Result> function1,
                                                                  Class<Input2> clazz2, Function<Input2, @Nullable Result> function2,
                                                                  Class<Input3> clazz3, Function<Input3, @Nullable Result> function3) {
        return List.of(of(clazz1, function1), of(clazz2, function2), of(clazz3, function3));
    }

    static <Input, Result> ConfigReader<Input, Result> of(Class<Input> clazz, Function<Input, @Nullable Result> function) {
        return new ConfigReaderImpl<>(clazz, function);
    }

    Class<Input> type();

    /**
     * Read an object into Result
     *
     * @param o
     *         object to read
     *
     * @return Result
     *
     * @see Deserializer#deserialize(Object)
     */
    @Nullable
    Result read(@Nullable Input o);

    final class ConfigReaderImpl<Input, Result> implements ConfigReader<Input, Result> {
        private final Class<Input> clazz;
        private final Function<Input, Result> function;

        public ConfigReaderImpl(Class<Input> clazz, Function<Input, @Nullable Result> function) {
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
        public Result read(@Nullable Input o) throws DeserializationException {
            return function.apply(o);
        }
    }
}

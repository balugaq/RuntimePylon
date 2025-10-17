package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.DeserializationException;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

/**
 * @author balugaq
 */
@NullMarked
public sealed interface ConfigReader<Input, Result> permits ConfigReader.ConfigReaderImpl {
    static <Input, Result> ConfigReader<Input, Result> of(Class<Input> clazz, Function<Input, @Nullable Result> function) {
        return new ConfigReaderImpl<>(clazz, function);
    }

    Class<Input> type();

    /**
     * Read an object into Result
     *
     * @param o object to read
     * @return Result
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

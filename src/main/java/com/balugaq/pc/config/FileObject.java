package com.balugaq.pc.config;

import com.balugaq.pc.exceptions.DeserializationException;
import com.balugaq.pc.exceptions.MissingFileException;
import com.balugaq.pc.util.ReflectionUtil;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * For the example:
 * <p>
 * <code>
 * <pre>
 * &#064;NoArgsConstructor(force  = true)
 * class Foo implements FileObject<Foo> {}
 * </pre>
 * </code>
 * <p>
 * The class `Foo` must be annotated with {@code @lombok.NoArgsConstructor(force = true)} to make
 * {@link #newDeserializer(Class)} work.
 *
 * @author balugaq
 * @see Pack
 */
@NullMarked
public interface FileObject<T> {
    /**
     * Create an instance of the object. All the data in this object are invalid. It just for call
     * {@link #deserialize(File)}.
     *
     * @return an instance of the object.
     *
     * @author balugaq
     * @see #deserialize(File)
     */
    @ApiStatus.Internal
    static <T extends FileObject<T>> T newDeserializer(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new DeserializationException(clazz, e);
        }
    }

    /**
     * Unserializes an object.
     *
     * @param o
     *         the object to deserialize, it may be {@link ConfigurationSection}, {@link ArrayList}, or primitive type.
     *
     * @return an instance of the object.
     *
     * @author balugaq
     * @see Deserializer#newDeserializer(Class)
     */
    @SneakyThrows
    default T deserialize(File o) throws DeserializationException, MissingFileException {
        for (FileReader<T> reader : readers()) {
            try {
                @SuppressWarnings("unchecked") T v = (T) ReflectionUtil.invokeMethod(reader, "read", o);
                if (v != null) return v;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw e.getCause();
            }
        }

        throw new DeserializationException(this.getClass());
    }

    List<FileReader<T>> readers();
}

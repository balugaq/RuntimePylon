package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.DeserializationException;
import com.balugaq.runtimepylon.exceptions.MissingArgumentException;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 */
@NullMarked
public interface FileObject<T> {
    List<FileReader<T>> readers();

    /**
     * Create an instance of the object.
     * All the data in this object are invalid.
     * It just for call {@link #deserialize(File)}.
     *
     * @return an instance of the object.
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
     * @param o the object to deserialize, it may be {@link ConfigurationSection}, {@link ArrayList}, or primitive type.
     * @return an instance of the object.
     * @author balugaq
     * @see Deserializable#newDeserializer(Class)
     */
    default T deserialize(File o) throws DeserializationException {
        for (FileReader<T> reader : readers()) {
            @SuppressWarnings("unchecked") T v = (T) ReflectionUtil.invokeMethod(reader, "read", o);
            if (v != null) return v;
        }

        throw new DeserializationException(this.getClass());
    }
}

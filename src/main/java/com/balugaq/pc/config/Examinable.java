package com.balugaq.pc.config;

import com.balugaq.pc.exceptions.ExamineFailedException;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.NullMarked;

/**
 * @param <T>
 *         the type of the object to examine
 *
 * @author balugaq
 */
@NullMarked
public interface Examinable<T> {
    /**
     * Examines the object.
     *
     * @return the object itself if the object is valid, otherwise throws an exception
     *
     * @throws ExamineFailedException
     *         if the object is invalid
     *
     * @see Pack#read(ConfigurationSection, Class, String)
     */
    T examine() throws ExamineFailedException;
}

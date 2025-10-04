package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.exceptions.ExamineFailedException;

/**
 * @param <T> the type of the object to examine
 * @author balugaq
 */
public interface Examinable<T> {
    /**
     * Examines the object.
     *
     * @return the object itself if the object is valid, otherwise throws an exception
     * @throws ExamineFailedException if the object is invalid
     */
    T examine() throws ExamineFailedException;
}

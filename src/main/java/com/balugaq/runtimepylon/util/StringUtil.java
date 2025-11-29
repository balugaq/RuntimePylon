package com.balugaq.runtimepylon.util;

import com.balugaq.runtimepylon.RuntimePylon;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class StringUtil {
    public static String simplifyPath(String path) {
        String pathToRemove = RuntimePylon.getInstance().getDataFolder().getAbsolutePath();
        return path.startsWith(pathToRemove) ? path.substring(pathToRemove.length()) : path;
    }
}

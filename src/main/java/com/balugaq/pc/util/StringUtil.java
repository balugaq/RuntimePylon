package com.balugaq.pc.util;

import com.balugaq.pc.PylonCustomizer;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class StringUtil {
    public static String simplifyPath(String path) {
        String pathToRemove = PylonCustomizer.getInstance().getDataFolder().getAbsolutePath();
        return path.startsWith(pathToRemove) ? path.substring(pathToRemove.length()) : path;
    }
}

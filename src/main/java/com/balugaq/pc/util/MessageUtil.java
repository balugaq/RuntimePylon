package com.balugaq.pc.util;

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author balugaq
 */
@NullMarked
public class MessageUtil {
    public static <T> Component humanizeListDisplay(List<T> list, Function<T, String> mapper, int limit) {
        if (list.isEmpty()) {
            return Component.text().build();
        }

        List<String> displayDirectly = new ArrayList<>();
        List<String> displayInHover = new ArrayList<>();
        int i = 0;
        for (T t : list) {
            String s = mapper.apply(t);
            if (i >= limit) {
                displayInHover.add(s);
            } else {
                displayDirectly.add(s);
                i++;
            }
        }

        var builder = Component.text();
        if (!displayDirectly.isEmpty()) {
            builder.append(Component.text(join(displayDirectly, ", ")));
        }
        if (!displayInHover.isEmpty()) {
            builder.append(Component.text(" and " + displayInHover.size() + " more..."));
            builder.hoverEvent(Component.text(join(displayInHover, ", ")));
        }

        return builder.build();
    }

    public static String join(List<String> list, String delimiter) {
        return String.join(delimiter, list.toArray(new String[0]));
    }
}

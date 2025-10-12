package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.pack.PackNamespace;
import com.balugaq.runtimepylon.exceptions.ExamineFailedException;
import com.balugaq.runtimepylon.exceptions.InvalidDescException;
import com.balugaq.runtimepylon.exceptions.UnknownPageException;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class PageDesc implements Deserializer<PageDesc> {
    @Nullable PackNamespace packNamespace = null;
    private final SimpleStaticGuidePage page;

    public PageDesc setPackNamespace(PackNamespace namespace) {
        if (this.packNamespace != null) throw new IllegalStateException("This method is for deserialization only");
        this.packNamespace = namespace;
        return this;
    }

    @Override
    public List<ConfigReader<?, PageDesc>> readers() {
        return List.of(
                ConfigReader.of(String.class, s -> {
                    String namespace;
                    String k;
                    if (s.contains(":")) {
                        namespace = s.substring(0, s.indexOf(":"));
                        k = s.substring(s.indexOf(":") + 1);
                    } else {
                        namespace = this.packNamespace.getNamespace();
                        k = s;
                    }

                    NamespacedKey key = NamespacedKey.fromString(namespace + ":" + k);
                    if (key == null) throw new InvalidDescException("Invalid page desc: " +s);

                    SimpleStaticGuidePage page = RuntimePylon.getGuidePages().get(key);
                    if (page == null) throw new UnknownPageException(key.toString());

                    return new PageDesc(page);
                })
        );
    }
}

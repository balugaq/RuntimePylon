package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.pack.PackNamespace;
import com.balugaq.runtimepylon.exceptions.UnknownPageException;
import io.github.pylonmc.pylon.core.guide.button.PageButton;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.UnknownNullability;
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
    private final NamespacedKey key;
    @UnknownNullability
    PackNamespace packNamespace = null;

    public PageDesc setPackNamespace(PackNamespace namespace) {
        if (this.packNamespace != null) throw new IllegalStateException("This method is for deserialization only");
        this.packNamespace = namespace;
        return this;
    }

    @Override
    public List<ConfigReader<?, PageDesc>> readers() {
        return ConfigReader.list(
                String.class, s -> {
                    PackNamespace namespace;
                    String k;
                    if (s.contains(":")) {
                        NamespacedKey key = NamespacedKey.fromString(s);
                        if (key != null && RuntimePylon.getGuidePages().get(key) != null) {
                            return new PageDesc(key);
                        }
                        namespace = Deserializer.PACK_DESC.deserialize(s.substring(0, s.indexOf(":"))).findPack().getPackNamespace();
                        k = s.substring(s.indexOf(":") + 1);
                    } else {
                        namespace = this.packNamespace;
                        k = s;
                    }

                    NamespacedKey key = InternalObjectID.of(k).register(namespace).key();
                    return new PageDesc(key);
                }
        );
    }

    public PageButton getPage() {
        PageButton page = RuntimePylon.getGuidePages().get(key);
        if (page == null) throw new UnknownPageException(key.toString());

        return page;
    }
}

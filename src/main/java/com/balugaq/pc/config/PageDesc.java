package com.balugaq.pc.config;

import com.balugaq.pc.PylonCustomizer;
import com.balugaq.pc.config.pack.PackNamespace;
import com.balugaq.pc.exceptions.UnknownPageException;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
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
                    if (s.contains(":")) {
                        NamespacedKey key = NamespacedKey.fromString(s);
                        if (key != null) {
                            return new PageDesc(key);
                        } else {
                            return new PageDesc(InternalObjectID.of(s.substring(s.lastIndexOf(':') + 1)).register(this.packNamespace).key());
                        }
                    }
                    return new PageDesc(InternalObjectID.of(s).register(this.packNamespace).key());
                }
        );
    }

    public SimpleStaticGuidePage getPage() {
        SimpleStaticGuidePage page = PylonCustomizer.getPages().get(key);
        if (page == null) throw new UnknownPageException(key.toString());

        return page;
    }
}

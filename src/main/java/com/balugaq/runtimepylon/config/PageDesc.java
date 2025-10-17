package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.pack.PackNamespace;
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
    private final NamespacedKey key;
    @Nullable PackNamespace packNamespace = null;

    public PageDesc setPackNamespace(PackNamespace namespace) {
        if (this.packNamespace != null) throw new IllegalStateException("This method is for deserialization only");
        this.packNamespace = namespace;
        return this;
    }

    @Override
    public List<ConfigReader<?, PageDesc>> readers() {
        return List.of(
                ConfigReader.of(String.class, s -> {
                    PackNamespace namespace;
                    String k;
                    if (s.contains(":")) {
                        NamespacedKey key = NamespacedKey.fromString(s);
                        if (key != null && RuntimePylon.getGuidePages().get(key) != null) {
                            return new PageDesc(key);
                        }
                        namespace = Deserializer.newDeserializer(PackDesc.class).deserialize(s.substring(0, s.indexOf(":"))).findPack().getPackNamespace();
                        k = s.substring(s.indexOf(":") + 1);
                    } else {
                        namespace = this.packNamespace;
                        k = s;
                    }

                    NamespacedKey key = InternalObjectID.of(k).with(namespace).register().key();
                    return new PageDesc(key);
                })
        );
    }

    public SimpleStaticGuidePage getPage() {
        SimpleStaticGuidePage page = RuntimePylon.getGuidePages().get(key);
        if (page == null) throw new UnknownPageException(key.toString());

        return page;
    }
}

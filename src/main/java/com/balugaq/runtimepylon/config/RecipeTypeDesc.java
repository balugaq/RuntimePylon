package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.config.pack.PackNamespace;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;
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
public class RecipeTypeDesc implements Deserializer<RecipeTypeDesc> {
    private final NamespacedKey key;
    @UnknownNullability
    PackNamespace packNamespace = null;

    public RecipeTypeDesc setPackNamespace(PackNamespace namespace) {
        if (this.packNamespace != null) throw new IllegalStateException("This method is for deserialization only");
        this.packNamespace = namespace;
        return this;
    }

    @Override
    public List<ConfigReader<?, RecipeTypeDesc>> readers() {
        return List.of(
                ConfigReader.of(
                        String.class, s -> {
                            PackNamespace namespace;
                            String k;
                            if (s.contains(":")) {
                                NamespacedKey key = NamespacedKey.fromString(s);
                                if (key != null && PylonRegistry.RECIPE_TYPES.get(key) != null) {
                                    return new RecipeTypeDesc(key);
                                }
                                namespace = Deserializer.newDeserializer(PackDesc.class).deserialize(s.substring(0, s.indexOf(":"))).findPack().getPackNamespace();
                                k = s.substring(s.indexOf(":") + 1);
                            } else {
                                namespace = this.packNamespace;
                                k = s;
                            }

                            NamespacedKey key = InternalObjectID.of(k).register(namespace).key();
                            return new RecipeTypeDesc(key);
                        }
                )
        );
    }

    @Nullable
    public RecipeType<?> findRecipeType() {
        return PylonRegistry.RECIPE_TYPES.get(key);
    }
}

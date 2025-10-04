package com.balugaq.runtimepylon.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

@Data
@AllArgsConstructor
@NullMarked
public class PreparedPage implements PostLoadable {
    final RegisteredObjectID id;
    final Material material;
    final boolean postLoad;
}

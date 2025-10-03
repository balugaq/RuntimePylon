package com.balugaq.runtimepylon.config;

import io.papermc.paper.datacomponent.DataComponentType;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class DataComponentDeserializeResult {
    private final Map<DataComponentType, Object> componentsToApply;
    private final Set<DataComponentType> componentsToRemove;
}

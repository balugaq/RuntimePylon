package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.RuntimePylon;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.display.BlockDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author balugaq
 */
@NullMarked
public
record MyMultiBlockComponent(List<? extends PylonSimpleMultiblock.MultiblockComponent> components,
                             List<BlockData> blockDataList) implements PylonSimpleMultiblock.MultiblockComponent {
    @Override
    public boolean matches(final Block block) {
        return components.stream().anyMatch(c -> c.matches(block));
    }

    @Override
    public UUID spawnGhostBlock(final Block block) {

        var display = new BlockDisplayBuilder()
                .material(blockDataList.getFirst().getMaterial())
                .glow(Color.WHITE)
                .transformation(new TransformBuilder().scale(0.5))
                .build(block.getLocation().toCenterLocation());
        EntityStorage.add(new PylonSimpleMultiblock.MultiblockGhostBlock(display, String.join(", ", blockDataList.stream().map(Object::toString).toList())));

        if (blockDataList.size() > 1) {
            AtomicInteger i = new AtomicInteger(0);
            RuntimePylon.runTaskTimer(
                    () -> {
                        while (display.isValid()) {
                            display.setBlock(blockDataList.get(i.getAndIncrement()));
                            i.set(i.get() % blockDataList.size());
                        }
                    }, 20, 20
            );
        }

        return display.getUniqueId();
    }
}

package com.balugaq.runtimepylon.config;

import com.balugaq.runtimepylon.config.pack.PackNamespace;
import com.balugaq.runtimepylon.object.CustomRecipeType;
import com.balugaq.runtimepylon.object.ItemStackProvider;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.item.Item;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author balugaq
 */
@NullMarked
public class GuiReader {
    public static Result read(ConfigurationSection section, PackNamespace namespace, @Nullable ScriptDesc scriptDesc) {
        if (!section.contains("structure")) {
            return Result.EMPTY;
        }

        List<String> structure = List.of(section.getString("structure").split("\n"));
        Pack.guiStructurePrecheck(structure);
        final AtomicReference<ItemStackProvider> guiProvider = new AtomicReference<>();
        if (section.contains("gui")) {
            ConfigurationSection sec = section.getConfigurationSection("gui");
            if (sec != null) {
                Char2ObjectOpenHashMap<Item> map = new Char2ObjectOpenHashMap<>();
                for (String key : sec.getKeys(false)) {
                    ItemStack itemStack = Deserializer.ITEMSTACK.deserialize(sec.get(key));
                    if (itemStack != null) {
                        map.put(key.charAt(0), ItemButton.from(itemStack));
                    }
                }
                guiProvider.set((c, r) -> {
                    if (map.containsKey(c)) {
                        return () -> map.get(c);
                    }

                    return CustomRecipeType.DEFAULT_GUI_PROVIDER.display(c, r);
                });
            }
        } else if (scriptDesc != null) {
            var exe = namespace.findScript(scriptDesc);
            if (exe != null && exe.isFunctionExists("provideGui")) {
                var r = exe.executeFunction(
                        "provideGui",
                        section, namespace
                );
                r.ifPresent(ItemStackProvider.class, guiProvider::set);
            }
        }

        return new Result(structure, guiProvider.get());
    }

    /**
     * @author balugaq
     */
    @NullMarked
    public record Result(List<String> structure, @Nullable ItemStackProvider provider) {
        @Unmodifiable
        public static final Result EMPTY = new Result(List.of(), null);
    }
}

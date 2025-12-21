package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.FileObject;
import com.balugaq.runtimepylon.config.FileReader;
import com.balugaq.runtimepylon.config.SaveditemDesc;
import com.balugaq.runtimepylon.exceptions.ExamineFailedException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A collection for saveditems
 *
 * @author balugaq
 */
@Data
@NoArgsConstructor(force = true)
@NullMarked
public class Saveditems implements FileObject<Saveditems> {
    // todo: update items
    private final Map<SaveditemDesc, ItemStack> items = new HashMap<>();

    @Nullable
    public ItemStack find(SaveditemDesc desc) {
        return items.get(desc);
    }

    @Override
    public List<FileReader<Saveditems>> readers() {
        return List.of(
                dir -> {
                    loadFiles(dir, dir.getPath());
                    return this;
                }
        );
    }

    public void loadFiles(File dir, String path) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                loadFiles(file, path + file.getName() + "/");
            } else {
                var serializer = Deserializer.SAVEDITEM_DESC;
                try {
                    var desc = serializer.deserialize(file);
                    ItemStack item = desc.getItemStack();
                    items.put(desc, item);
                } catch (ExamineFailedException ignored) {
                }
            }
        }
    }
}

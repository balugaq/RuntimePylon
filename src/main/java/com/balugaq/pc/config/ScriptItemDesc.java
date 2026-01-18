package com.balugaq.pc.config;

import com.balugaq.pc.exceptions.ExamineFailedException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
@Getter
public class ScriptItemDesc implements Examinable<ScriptItemDesc>, Deserializer<ScriptItemDesc> {
    private final ItemStack item; //todo: solve it
    private final String scriptPath;

    @Override
    public List<ConfigReader<?, ScriptItemDesc>> readers() {
        return List.of();
    }

    @Override
    public ScriptItemDesc examine() throws ExamineFailedException {
        if (!scriptPath.matches("[A-Za-z0-9_+\\-/]+")) {
            throw new ExamineFailedException("Script Desc must be [A-Za-z0-9_+-]+");
        }
        return this;
    }

    public Item getInvUIItem() {
        return new SimpleItem(item, c -> {

        });
    }
}

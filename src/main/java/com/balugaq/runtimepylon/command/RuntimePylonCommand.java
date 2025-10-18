package com.balugaq.runtimepylon.command;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PackDesc;
import com.balugaq.runtimepylon.config.PackManager;
import com.balugaq.runtimepylon.config.StackWalker;
import com.balugaq.runtimepylon.exceptions.ExamineFailedException;
import com.balugaq.runtimepylon.util.Debug;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class RuntimePylonCommand {
    public final int THRESHOLD = 1 << 14;
    public final LiteralCommandNode<CommandSourceStack> ROOT = Commands.literal("runtime")
            .then(Commands.literal("clearsettings")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.clearsettings"))
                    .executes(RuntimePylonCommand::clearSettings)
            )
            .then(Commands.literal("clearrecipes")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.clearrecipes"))
                    .executes(RuntimePylonCommand::clearRecipes)
            )
            .then(Commands.literal("clearlang")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.clearlang"))
                    .executes(RuntimePylonCommand::clearLang)
            )
            .then(Commands.literal("clearall")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.clearall"))
                    .executes(RuntimePylonCommand::clearAll)
            )
            .then(Commands.literal("loadpacks")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.loadpacks"))
                    .executes(RuntimePylonCommand::loadPacks)
            )
            .then(Commands.literal("unloadpacks")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.unloadpacks"))
                    .executes(RuntimePylonCommand::unloadPacks)
            )
            .then(Commands.literal("reloadpacks")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.reloadpacks"))
                    .executes(RuntimePylonCommand::reloadPacks)
            )
            .then(Commands.literal("reloadplugin")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.reloadplugin"))
                    .executes(RuntimePylonCommand::reloadPlugin)
            )
            .then(Commands.literal("help")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.help"))
                    .executes(RuntimePylonCommand::help)
            )
            .then(Commands.literal("saveitem")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.saveitem"))
                    .executes(RuntimePylonCommand::saveItem)
            )
            .then(Commands.literal("saveitem")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.saveitem"))
                    .then(Commands.argument("filename", StringArgumentType.string()).executes(RuntimePylonCommand::saveItem))
            )
            .then(Commands.literal("saveitem")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.saveitem"))
                    .then(Commands.argument("packname", StringArgumentType.string())
                            .suggests((ctx, builder) -> {
                                for (Pack pack : PackManager.getPacks()) {
                                    builder.suggest(pack.getPackID().getId());
                                }
                                return builder.buildFuture();
                            })
                            .then(Commands.argument("filename", StringArgumentType.string())
                                    .executes(RuntimePylonCommand::saveItem)))
            )
            .build();

    private int clearSettings(CommandContext<CommandSourceStack> ctx) {
        PackManager.getPacks().forEach(pack -> deleteFolder(pack.getSettingsFolder()));
        return Command.SINGLE_SUCCESS;
    }

    private int clearRecipes(CommandContext<CommandSourceStack> ctx) {
        PackManager.getPacks().forEach(pack -> deleteFolder(pack.getRecipesFolder()));
        return Command.SINGLE_SUCCESS;
    }

    private int clearLang(CommandContext<CommandSourceStack> ctx) {
        PackManager.getPacks().forEach(pack -> deleteFolder(pack.getLangFolder()));
        return Command.SINGLE_SUCCESS;
    }

    private int clearAll(CommandContext<CommandSourceStack> ctx) {
        clearSettings(ctx);
        clearRecipes(ctx);
        clearLang(ctx);
        StackWalker.getPositions().clear();
        return Command.SINGLE_SUCCESS;
    }

    private int loadPacks(CommandContext<CommandSourceStack> ctx) {
        RuntimePylon.getPackManager().loadPacks();
        return Command.SINGLE_SUCCESS;
    }

    private int unloadPacks(CommandContext<CommandSourceStack> ctx) {
        RuntimePylon.getPackManager().destroy();
        return Command.SINGLE_SUCCESS;
    }

    private int reloadPacks(CommandContext<CommandSourceStack> ctx) {
        unloadPacks(ctx);
        loadPacks(ctx);
        return Command.SINGLE_SUCCESS;
    }

    private int reloadPlugin(CommandContext<CommandSourceStack> ctx) {
        clearAll(ctx);
        unloadPacks(ctx);
        new PluginDisableEvent(RuntimePylon.getInstance()).callEvent();
        RuntimePylon.getInstance().registerWithPylon();
        RuntimePylon.getPackManager().loadPacks();
        return Command.SINGLE_SUCCESS;
    }

    private int help(CommandContext<CommandSourceStack> ctx) {
        // todo
        return Command.SINGLE_SUCCESS;
    }

    private int saveItem(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>You must be a player to use this command");
            return Command.SINGLE_SUCCESS;
        }

        String packName;
        String fileName;
        try {
            packName = ctx.getArgument("packname", Pack.class).getPackID().getId();
        } catch (Exception e) {
            packName = null;
        }
        try {
            fileName = ctx.getArgument("filename", String.class);
        } catch (Exception e) {
            fileName = null;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null || itemStack.getType() == Material.AIR) itemStack = player.getInventory().getItemInOffHand();
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            sender.sendRichMessage("<red>You must be holding an item to use this command");
            return Command.SINGLE_SUCCESS;
        }

        List<Pack> packs = PackManager.getPacks();
        if (packs.isEmpty()) {
            sender.sendRichMessage("<red>You haven't set up a pack yet");
            return Command.SINGLE_SUCCESS;
        }

        Pack pack = null;
        if (packName == null) {
            if (packs.size() != 1) {
                sender.sendRichMessage("<red>You have multiple packs set up, please specify which pack you want to save the item to");
                return Command.SINGLE_SUCCESS;
            }

            pack = packs.getFirst();
        } else {
            for (Pack p : packs) {
                if (p.getPackID().getId().equals(packName)) {
                    pack = p;
                    break;
                }
            }
            if (pack == null) {
                sender.sendRichMessage("<red>Unknown pack");
                return Command.SINGLE_SUCCESS;
            }
        }

        File saveditemsFolder = new File(pack.getDir(), "saveditems");
        if (!saveditemsFolder.exists()) saveditemsFolder.mkdirs();

        if (fileName == null) {
            for (int i = 0; i < THRESHOLD; i++) {
                fileName = "" + i;
                File file = new File(saveditemsFolder, fileName + ".yml");
                if (!file.exists()) {
                    write(player, file, itemStack);
                    return Command.SINGLE_SUCCESS;
                }
            }

            sender.sendRichMessage("<red>Failed to save item automatically");
        } else {
            write(player, new File(saveditemsFolder, fileName + ".yml"), itemStack);
        }

        return Command.SINGLE_SUCCESS;
    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    private static void write(Player player, File file, ItemStack itemStack) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Debug.severe(e);
                player.sendRichMessage("<red>Failed to save item");
                return;
            }
        }
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", itemStack);
        try {
            config.save(file);
            Component name1 = itemStack.getData(DataComponentTypes.ITEM_NAME);
            Component name2 = itemStack.getData(DataComponentTypes.CUSTOM_NAME);
            player.sendRichMessage("<green>Saved " + GlobalTranslator.render(name1 == null ? name2 : name1, player.locale()) + " to " + file.getName());
        } catch (IOException e) {
            Debug.severe(e);
            player.sendRichMessage("<red>Failed to save item");
        }
    }
}

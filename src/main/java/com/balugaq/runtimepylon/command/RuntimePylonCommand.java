package com.balugaq.runtimepylon.command;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.Language;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PackDesc;
import com.balugaq.runtimepylon.config.PluginDesc;
import com.balugaq.runtimepylon.config.StackFormatter;
import com.balugaq.runtimepylon.config.pack.Author;
import com.balugaq.runtimepylon.config.pack.Contributor;
import com.balugaq.runtimepylon.config.pack.GitHubUpdateLink;
import com.balugaq.runtimepylon.config.pack.WebsiteLink;
import com.balugaq.runtimepylon.manager.PackManager;
import com.balugaq.runtimepylon.util.Debug;
import com.balugaq.runtimepylon.util.MessageUtil;
import com.balugaq.runtimepylon.util.MinecraftVersion;
import com.balugaq.runtimepylon.util.ReflectionUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author balugaq
 */
@SuppressWarnings({"ConstantValue", "SameReturnValue", "ResultOfMethodCallIgnored", "UnstableApiUsage"})
@UtilityClass
@NullMarked
public class RuntimePylonCommand {
    public static final int MAX_SAVEDITEMS = 1 << 14;
    //@formatter:off
    public static final LiteralCommandNode<CommandSourceStack> ROOT = Commands.literal("runtime")
        .then(Commands.literal("clearsettings")
            .requires(source -> source.getSender().hasPermission("runtimepylon.command.clearsettings"))
            .executes(RuntimePylonCommand::clearSettings)
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
        .then(Commands.literal("info")
            .requires(source -> source.getSender().hasPermission("runtimepylon.command.info"))
            .then(Commands.argument("packname", StringArgumentType.string())
                .suggests((ctx, builder) -> {
                    for (Pack pack : PackManager.getPacks()) {
                        builder.suggest(pack.getPackID().getId());
                    }
                    return builder.buildFuture();
                }).executes(RuntimePylonCommand::info))
        )
        .then(Commands.literal("info")
            .requires(source -> source.getSender().hasPermission("runtimepylon.command.info"))
            .executes(RuntimePylonCommand::info)
        )
        .build();
    //@formatter:on

    private int info(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        try {
            Pack pack = PackManager.findPack(new PackDesc(ctx.getArgument("packname", String.class)));
            if (pack == null) {
                sender.sendRichMessage("<red>Unknown pack");
                return Command.SINGLE_SUCCESS;
            }
            sendMessage(sender, "=".repeat(20));
            sendMessage(sender, "Pack Dir: ", pack.getDir().getPath());
            sendMessage(sender, "Pack ID: ", pack.getPackID().getId());
            sendMessage(sender, "Pack Namespace: ", pack.getPackNamespace().getNamespace());
            sendMessage(sender, "Pack Version: ", pack.getPackVersion().getVersion());
            sendMessage(sender, "Pack Min API Version: ", pack.getPackMinAPIVersion(), MinecraftVersion::humanize);
            sendMessage(sender, "Pack Max API Version: ", pack.getPackMaxAPIVersion(), MinecraftVersion::humanize);
            showList(sender, Component.text("Pack Load Before: "), pack.getPackLoadBefores(), PackDesc::getId, 3);
            showList(sender, Component.text("Pack Soft Dependencies: "), pack.getPackSoftDependencies(), PackDesc::getId, 3);
            showList(sender, Component.text("Pack Dependencies: "), pack.getPackDependencies(), PackDesc::getId, 3);
            showList(sender, Component.text("Plugin Dependencies: "), pack.getPluginDependencies(), PluginDesc::getId, 3);
            showList(sender, Component.text("Authors: "), pack.getAuthors(), Author::getName, 5);
            showList(sender, Component.text("Contributors: "), pack.getContributors(), Contributor::getName, 5);
            showList(sender, Component.text("Website Links: "), pack.getWebsiteLinks(), WebsiteLink::getLink, 5);
            sendMessage(sender, "GitHub Update Link: ", pack.getGithubUpdateLink(), GitHubUpdateLink::getLink);
            showList(sender, Component.text("Languages: "), pack.getLanguages(), Language::localeCode, 5);
            List<String> registryInfo = getRegistryInfo(pack);
            showList(sender, Component.text("Registered "), registryInfo);
            sendMessage(sender, "=".repeat(20));
        } catch (Exception ignored) {
            sendMessage(sender, "Version: ", RuntimePylon.getInstance().getPluginMeta().getVersion());
            showList(sender, Component.text("Packs: "), PackManager.getPacks(), pack -> pack.getPackID().getId(), 5);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static List<String> getRegistryInfo(final Pack pack) {
        List<String> registryInfo = new ArrayList<>();
        if (pack.getPages() != null)
            registryInfo.add(pack.getPages().getPages().size() + " Pages");
        if (pack.getItems() != null)
            registryInfo.add(pack.getItems().getItems().size() + " Items");
        if (pack.getBlocks() != null)
            registryInfo.add(pack.getBlocks().getBlocks().size() + " Blocks");
        if (pack.getFluids() != null)
            registryInfo.add(pack.getFluids().getFluids().size() + " Fluids");
        if (pack.getRecipeTypes() != null)
            registryInfo.add(pack.getRecipeTypes().getRecipeTypes().size() + " Recipe Types");
        if (pack.getRecipes() != null)
            registryInfo.add(pack.getRecipes().getLoadedRecipes() + " Recipes");
        if (pack.getScripts() != null)
            registryInfo.add(pack.getScripts().getScripts().size() + " Scripts");
        return registryInfo;
    }

    private void showList(CommandSender sender, Component prefix, @Nullable List<String> list) {
        showList(sender, prefix, list, s -> s, Integer.MAX_VALUE);
    }

    private void showList(CommandSender sender, Component prefix, @Nullable List<String> list, int limit) {
        showList(sender, prefix, list, s -> s, limit);
    }

    private <T> void showList(CommandSender sender, Component prefix, @Nullable List<T> list, Function<T, String> mapper, int limit) {
        if (list != null && !list.isEmpty())
            sender.sendMessage(prefix.color(TextColor.color(0x00d000)).append(MessageUtil.humanizeListDisplay(list, mapper, limit)));
    }

    private <T> void showList(CommandSender sender, Component prefix, @Nullable List<T> list, Function<T, String> mapper) {
        showList(sender, prefix, list, mapper, Integer.MAX_VALUE);
    }

    private void sendMessage(CommandSender sender, String text) {
        sendMessage(sender, "", text);
    }

    private void sendMessage(CommandSender sender, String prefix, String text) {
        sendMessage(sender, prefix, text, s -> s);
    }

    private <T> void sendMessage(CommandSender sender, String prefix, @Nullable T object, Function<T, String> mapper) {
        if (object != null) {
            sender.sendMessage(Component.text(prefix).color(TextColor.color(0x00d000)).append(Component.text(mapper.apply(object))));
        }
    }

    private int clearSettings(CommandContext<CommandSourceStack> ctx) {
        PackManager.getPacks().forEach(pack -> deleteFolder(pack.getSettingsFolder()));
        return Command.SINGLE_SUCCESS;
    }

    private int clearLang(CommandContext<CommandSourceStack> ctx) {
        PackManager.getPacks().forEach(pack -> deleteFolder(pack.getLangFolder()));
        return Command.SINGLE_SUCCESS;
    }

    private int clearAll(CommandContext<CommandSourceStack> ctx) {
        clearSettings(ctx);
        clearLang(ctx);
        StackFormatter.getPositions().clear();
        return Command.SINGLE_SUCCESS;
    }

    private int reloadPacks(CommandContext<CommandSourceStack> ctx) {
        unloadPacks(ctx);
        loadPacks(ctx);
        return Command.SINGLE_SUCCESS;
    }

    private int unloadPacks(CommandContext<CommandSourceStack> ctx) {
        RuntimePylon.getPackManager().destroy();
        try {
            ReflectionUtil.getStaticValue(PylonUtils.class, "globalConfigCache", Map.class).clear();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Debug.warning(e);
        }
        return Command.SINGLE_SUCCESS;
    }

    private int loadPacks(CommandContext<CommandSourceStack> ctx) {
        RuntimePylon.getPackManager().loadPacks();
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
        ctx.getArgument("addon", String.class);
        ctx.getSource().getSender().sendMessage(Component.text(
                "commands: clearsettings, clearrecipes, clearlang, clearall, loadpacks, unloadpacks, reloadpacks, reloadplugin, info, help, saveitem"
        ));
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
            packName = ctx.getArgument("packname", String.class);
        } catch (Exception e) {
            packName = null;
        }
        try {
            fileName = ctx.getArgument("filename", String.class);
        } catch (Exception e) {
            fileName = null;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null || itemStack.getType() == Material.AIR)
            itemStack = player.getInventory().getItemInOffHand();
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
            for (int i = 0; i < MAX_SAVEDITEMS; i++) {
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
}

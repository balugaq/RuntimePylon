package com.balugaq.runtimepylon.command;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.StackWalker;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.experimental.UtilityClass;
import org.bukkit.event.server.PluginDisableEvent;

@UtilityClass
public class RuntimePylonCommand {
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
            .then(Commands.literal("reloadplugin")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.reloadplugin"))
                    .executes(RuntimePylonCommand::reloadPlugin)
            )
            .then(Commands.literal("help")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.help"))
                    .executes(RuntimePylonCommand::help)
            )
            .build();

    private int clearSettings(CommandContext<CommandSourceStack> ctx) {
        Pack.settingsFolder.delete();
        return Command.SINGLE_SUCCESS;
    }

    private int clearRecipes(CommandContext<CommandSourceStack> ctx) {
        Pack.recipesFolder.delete();
        return Command.SINGLE_SUCCESS;
    }

    private int clearLang(CommandContext<CommandSourceStack> ctx) {
        Pack.langFolder.delete();
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

    private int reloadPlugin(CommandContext<CommandSourceStack> ctx) {
        clearAll();
        RuntimePylon.getPackManager().destroy();
        new PluginDisableEvent(RuntimePylon.getInstance()).callEvent();
        RuntimePylon.getInstance().registerWithPylon();
        RuntimePylon.getPackManager().loadPacks();
        return Command.SINGLE_SUCCESS;
    }

    private int help(CommandContext<CommandSourceStack> ctx) {
        // todo
        return Command.SINGLE_SUCCESS;
    }
}

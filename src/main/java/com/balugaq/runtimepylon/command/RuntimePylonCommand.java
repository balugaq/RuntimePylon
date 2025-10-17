package com.balugaq.runtimepylon.command;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.Pack;
import com.mojang.brigadier.Command;
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
                    .executes(context -> clearSettings())
            )
            .then(Commands.literal("clearrecipes")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.clearrecipes"))
                    .executes(context -> clearRecipes())
            )
            .then(Commands.literal("clearlang")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.clearlang"))
                    .executes(context -> clearLang())
            )
            .then(Commands.literal("clearall")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.clearall"))
                    .executes(context -> clearAll())
            )
            .then(Commands.literal("loadpacks")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.loadpacks"))
                    .executes(context -> loadPacks())
            )
            .then(Commands.literal("reloadplugin")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.reloadplugin"))
                    .executes(context -> reloadPlugin())
            )
            .then(Commands.literal("help")
                    .requires(source -> source.getSender().hasPermission("runtimepylon.command.help"))
                    .executes(context -> help())
            )
            .build();

    private int clearSettings() {
        Pack.settingsFolder.delete();
        return Command.SINGLE_SUCCESS;
    }

    private int clearRecipes() {
        Pack.recipesFolder.delete();
        return Command.SINGLE_SUCCESS;
    }

    private int clearLang() {
        Pack.langFolder.delete();
        return Command.SINGLE_SUCCESS;
    }

    private int clearAll() {
        clearSettings();
        clearRecipes();
        clearLang();
        return Command.SINGLE_SUCCESS;
    }

    private int loadPacks() {
        RuntimePylon.getPackManager().loadPacks();
        return Command.SINGLE_SUCCESS;
    }

    private int reloadPlugin() {
        clearAll();
        RuntimePylon.getPackManager().destroy();
        new PluginDisableEvent(RuntimePylon.getInstance()).callEvent();
        RuntimePylon.getInstance().registerWithPylon();
        RuntimePylon.getPackManager().loadPacks();
        return Command.SINGLE_SUCCESS;
    }

    private int help() {
        // todo
        return Command.SINGLE_SUCCESS;
    }
}

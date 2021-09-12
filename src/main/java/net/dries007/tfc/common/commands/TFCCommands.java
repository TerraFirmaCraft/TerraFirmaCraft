/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import com.mojang.brigadier.CommandDispatcher;

public final class TFCCommands
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        // Register all new commands as sub commands of the `tfc` root
        dispatcher.register(Commands.literal("tfc")
            .then(ClearWorldCommand.create())
            .then(HeatCommand.create())
            .then(PlayerCommand.create())
            .then(TreeCommand.create())
            .then(LocateVeinCommand.create())
        );

        // For command modifications / replacements, we register directly
        // First, remove the vanilla command by the same name
        // This seems to work. It does leave the command still lying around, but it shouldn't matter as we replace it anyway
        dispatcher.getRoot().getChildren().removeIf(node -> node.getName().equals("time"));
        dispatcher.register(TimeCommand.create());
    }
}
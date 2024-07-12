/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.command.EnumArgument;

import net.dries007.tfc.common.component.forge.ForgingBonus;

public final class ForgeCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("forge").requires(source -> source.hasPermission(2))
            .then(Commands.argument("bonus", EnumArgument.enumArgument(ForgingBonus.class))
                .executes(cmd -> applyForgingBonus(cmd.getSource(), cmd.getArgument("bonus", ForgingBonus.class)))
            );
    }

    private static int applyForgingBonus(CommandSourceStack source, ForgingBonus bonus) throws CommandSyntaxException
    {
        final ServerPlayer player = source.getPlayerOrException();
        final ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty())
        {
            ForgingBonus.set(stack, bonus);
        }
        return Command.SINGLE_SUCCESS;
    }
}

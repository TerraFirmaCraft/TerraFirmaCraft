/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;

public final class HeatCommand
{
    private static final String QUERY_HEAT = "tfc.commands.heat.set_heat";

    public static LiteralArgumentBuilder<CommandSource> create()
    {
        return Commands.literal("heat").requires(source -> source.hasPermission(2))
            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                .executes(cmd -> heatItem(cmd.getSource(), IntegerArgumentType.getInteger(cmd, "value")))
            );
    }

    private static int heatItem(CommandSource source, int value) throws CommandSyntaxException
    {
        final ServerPlayerEntity player = source.getPlayerOrException();
        final ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty())
        {
            stack.getCapability(HeatCapability.CAPABILITY).ifPresent(heat ->
            {
                heat.setTemperature(value);
                source.sendSuccess(new TranslationTextComponent(QUERY_HEAT, value), true);
            });
        }
        return Command.SINGLE_SUCCESS;
    }
}
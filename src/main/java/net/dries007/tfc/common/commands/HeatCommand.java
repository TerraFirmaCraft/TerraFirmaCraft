/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;

public final class HeatCommand
{
    private static final String SET_HEAT = "tfc.commands.heat.set_heat";

    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("heat").requires(source -> source.hasPermission(2))
            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                .executes(cmd -> heatItem(cmd.getSource(), IntegerArgumentType.getInteger(cmd, "value")))
            );
    }

    private static int heatItem(CommandSourceStack source, int value) throws CommandSyntaxException
    {
        final ServerPlayer player = source.getPlayerOrException();
        final ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty())
        {
            final @Nullable IHeat heat = HeatCapability.get(stack);
            if (heat != null)
            {
                heat.setTemperature(value);
                source.sendSuccess(() -> Component.translatable(SET_HEAT, value), true);
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
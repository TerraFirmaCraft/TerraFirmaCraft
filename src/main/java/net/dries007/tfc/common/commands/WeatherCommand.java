/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import net.dries007.tfc.util.tracker.WorldTracker;

public class WeatherCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("weather").requires(source -> source.hasPermission(2))
            .then(Commands.literal("enable")
                .executes(context -> setEnabled(context.getSource(), true))
            )
            .then(Commands.literal("disable")
                .executes(context -> setEnabled(context.getSource(), false))
            );
    }

    private static int setEnabled(CommandSourceStack source, boolean enabled)
    {
        WorldTracker.get(source.getLevel()).setWeatherEnabled(enabled);
        source.sendSuccess(() -> Component.translatable("tfc.commands.weather_enabled." + enabled), true);
        return Command.SINGLE_SUCCESS;
    }
}

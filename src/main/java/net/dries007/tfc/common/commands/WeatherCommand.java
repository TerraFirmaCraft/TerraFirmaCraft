/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tracker.WorldTrackerCapability;

public class WeatherCommand
{
    private static final int DEFAULT_TIME = 6000;
    private static final float DEFAULT_INTENSITY = 0.5f;

    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("weather").requires(source -> source.hasPermission(2))
            .then(Commands.literal("clear")
                .executes(context -> setClear(context.getSource(), DEFAULT_TIME))
                .then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000))
                    .executes(context -> setClear(context.getSource(), IntegerArgumentType.getInteger(context, "duration") * 20))
                )
            )
            .then(Commands.literal("rain")
                .executes(context -> setRain(context.getSource(), DEFAULT_TIME, DEFAULT_INTENSITY))
                .then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000))
                    .executes(context -> setRain(context.getSource(), IntegerArgumentType.getInteger(context, "duration") * 20, DEFAULT_INTENSITY))
                    .then(Commands.argument("intensity", FloatArgumentType.floatArg(0, 1))
                        .executes(context -> setRain(context.getSource(), IntegerArgumentType.getInteger(context, "duration") * 20, FloatArgumentType.getFloat(context, "intensity")))
                    )
                )
            )
            .then(Commands.literal("thunder")
                .executes(context -> setThunder(context.getSource(), DEFAULT_TIME, DEFAULT_INTENSITY))
                .then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000))
                    .executes(context -> setThunder(context.getSource(), IntegerArgumentType.getInteger(context, "duration") * 20, DEFAULT_INTENSITY))
                    .then(Commands.argument("intensity", FloatArgumentType.floatArg(0, 1))
                        .executes(context -> setThunder(context.getSource(), IntegerArgumentType.getInteger(context, "duration") * 20, FloatArgumentType.getFloat(context, "intensity")))
                    )
                )
            );
    }

    private static int setClear(CommandSourceStack source, int time)
    {
        source.getLevel().setWeatherParameters(time, 0, false, false);
        source.sendSuccess(Helpers.translatable("commands.weather.set.clear"), true);
        return time;
    }

    private static int setRain(CommandSourceStack source, int time, float intensity)
    {
        source.getLevel().setWeatherParameters(0, time, true, false);
        source.getLevel().getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.setWeatherData(time, intensity));
        source.sendSuccess(Helpers.translatable("commands.weather.set.rain"), true);
        return time;
    }

    private static int setThunder(CommandSourceStack source, int time, float intensity)
    {
        source.getLevel().setWeatherParameters(0, time, true, true);
        source.getLevel().getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.setWeatherData(time, intensity));
        source.sendSuccess(Helpers.translatable("commands.weather.set.thunder"), true);
        return time;
    }
}

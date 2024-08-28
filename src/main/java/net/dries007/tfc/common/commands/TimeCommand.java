/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.tracker.WeatherHelpers;

public final class TimeCommand
{
    private static final String DAYTIME = "tfc.commands.time.query.daytime";
    private static final String GAME_TIME = "tfc.commands.time.query.game_time";
    private static final String DAY = "tfc.commands.time.query.day";
    private static final String PLAYER_TICKS = "tfc.commands.time.query.player_ticks";
    private static final String CALENDAR_TICKS = "tfc.commands.time.query.calendar_ticks";

    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("time")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("set")
                .then(Commands.literal("monthlength")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> setMonthLength(IntegerArgumentType.getInteger(context, "value")))
                    )
                )
                .then(Commands.literal("day")
                    .executes(context -> setTime(context.getSource().getServer(), 1000))
                )
                .then(Commands.literal("noon")
                    .executes(context -> setTime(context.getSource().getServer(), 6000))
                )
                .then(Commands.literal("night")
                    .executes(context -> setTime(context.getSource().getServer(), 13000))
                )
                .then(Commands.literal("midnight")
                    .executes(context -> setTime(context.getSource().getServer(), 18000))
                )
            )
            .then(Commands.literal("add")
                .then(Commands.literal("years")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(IntegerArgumentType.getInteger(context, "value") * Calendars.SERVER.getCalendarTicksInYear()))
                    )
                )
                .then(Commands.literal("months")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(IntegerArgumentType.getInteger(context, "value") * Calendars.SERVER.getCalendarTicksInMonth()))
                    )
                )
                .then(Commands.literal("days")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(IntegerArgumentType.getInteger(context, "value") * (long) ICalendar.TICKS_IN_DAY))
                    )
                )
                .then(Commands.literal("ticks")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(IntegerArgumentType.getInteger(context, "value")))
                    )
                )
            )
            .then(Commands.literal("skip-to-weather-rain")
                .executes(context -> skipTo(context.getSource(), true))
            )
            .then(Commands.literal("skip-to-weather-clear")
                .executes(context -> skipTo(context.getSource(), false))
            )
            .then(Commands.literal("query")
                .then(Commands.literal("daytime")
                    .executes(context -> sendQueryResults(context.getSource(), DAYTIME, Calendars.SERVER.getCalendarDayTime()))
                )
                .then(Commands.literal("gametime")
                    .executes(context -> sendQueryResults(context.getSource(), GAME_TIME, context.getSource().getLevel().getGameTime()))
                )
                .then(Commands.literal("day")
                    .executes(context -> sendQueryResults(context.getSource(), DAY, Calendars.SERVER.getTotalDays()))
                )
                .then(Commands.literal("ticks")
                    .executes(context -> sendQueryResults(context.getSource(), PLAYER_TICKS, Calendars.SERVER.getTicks()))
                )
                .then(Commands.literal("calendarticks")
                    .executes(context -> sendQueryResults(context.getSource(), CALENDAR_TICKS, Calendars.SERVER.getCalendarTicks()))
                )
            );
    }

    private static int setMonthLength(int months)
    {
        Calendars.SERVER.setMonthLength(months);
        return Command.SINGLE_SUCCESS;
    }

    private static int setTime(MinecraftServer server, int dayTime)
    {
        for (ServerLevel world : server.getAllLevels())
        {
            long dayTimeJump = dayTime - (world.getDayTime() % ICalendar.TICKS_IN_DAY);
            if (dayTimeJump < 0)
            {
                dayTimeJump += ICalendar.TICKS_IN_DAY;
            }
            world.setDayTime(world.getDayTime() + dayTimeJump);
        }
        Calendars.SERVER.setTimeFromDayTime(dayTime);
        return Command.SINGLE_SUCCESS;
    }

    private static int addTime(long ticksToAdd)
    {
        Calendars.SERVER.setTimeFromCalendarTime(Calendars.SERVER.getCalendarTicks() + ticksToAdd);
        return Command.SINGLE_SUCCESS;
    }

    private static int skipTo(CommandSourceStack source, boolean rain)
    {
        final ClimateModel model = Climate.get(source.getLevel());
        final long calendarTick = Calendars.SERVER.getCalendarTicks();
        final float rainfall = model.getRainfall(source.getLevel(), BlockPos.containing(source.getPosition()));
        for (int tick = 0; tick < 400_000; tick += 1_000)
        {
            if (WeatherHelpers.isPrecipitating(model.getRain(calendarTick + tick), rainfall) == rain)
            {
                final Component feedback = Component.translatable("tfc.commands.time.skip_forward", tick);
                Calendars.SERVER.setTimeFromCalendarTime(calendarTick + tick);
                source.sendSuccess(() -> feedback, true);
                return Command.SINGLE_SUCCESS;
            }
        }
        return 0;
    }

    private static int sendQueryResults(CommandSourceStack source, String translationKey, long value)
    {
        source.sendSuccess(() -> Component.translatable(translationKey, (int) value), false);
        return Command.SINGLE_SUCCESS;
    }
}
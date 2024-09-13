/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import net.dries007.tfc.util.calendar.Calendar;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.tracker.WeatherHelpers;

public final class TimeCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("time")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("set")
                .then(Commands.literal("dayLength")
                    .then(Commands.literal("vanilla").executes(c -> setDayLength(c, 20))) // Default Vanilla
                    .then(Commands.literal("default").executes(c -> setDayLength(c, 24))) // Default TFC
                    .then(Commands.literal("disabled").executes(c -> setDayLength(c, -1)))
                    .then(Commands.literal("realtime").executes(c -> setDayLength(c, 24 * 60)))
                    .then(Commands.argument("minutes", IntegerArgumentType.integer(1))
                        .executes(c -> setDayLength(c, IntegerArgumentType.getInteger(c, "minutes")))
                    )
                )
                .then(Commands.literal("monthLength")
                    .then(Commands.literal("default").executes(c -> setMonthLength(c, Calendar.DEFAULT_MONTH_LENGTH)))
                    .then(Commands.literal("realtime").executes(c -> setMonthLength(c, 30)))
                    .then(Commands.argument("days", IntegerArgumentType.integer(1, 1000))
                        .executes(c -> setMonthLength(c, IntegerArgumentType.getInteger(c, "days")))
                    )
                )
                .then(Commands.literal("day").executes(c -> setTimeFromDayTime(c, 0.3f)))
                .then(Commands.literal("noon").executes(c -> setTimeFromDayTime(c, 0.5f)))
                .then(Commands.literal("night").executes(c -> setTimeFromDayTime(c, 0.8f)))
                .then(Commands.literal("midnight").executes(c -> setTimeFromDayTime(c, 0f)))
                .then(Commands.literal("rain").executes(c -> setTimeFromWeather(c, true)))
                .then(Commands.literal("clear").executes(c -> setTimeFromWeather(c, false)))
            )
            .then(Commands.literal("add")
                .then(Commands.literal("years")
                    .then(Commands.argument("years", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(context, IntegerArgumentType.getInteger(context, "years") * Calendars.SERVER.getCalendarTicksInYear()))
                    )
                )
                .then(Commands.literal("months")
                    .then(Commands.argument("months", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(context, IntegerArgumentType.getInteger(context, "months") * Calendars.SERVER.getCalendarTicksInMonth()))
                    )
                )
                .then(Commands.literal("days")
                    .then(Commands.argument("days", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(context, IntegerArgumentType.getInteger(context, "days") * (long) ICalendar.TICKS_IN_DAY))
                    )
                )
                .then(Commands.argument("ticks", IntegerArgumentType.integer(1))
                    .executes(context -> addTime(context, IntegerArgumentType.getInteger(context, "ticks")))
                )
            );
    }

    private static int setDayLength(CommandContext<CommandSourceStack> context, final int dayLengthInMinutes)
    {
        // TPS = 20 t/s = 1200 t/m
        // 1 Day = 24_000 ct
        // 1 ct = rate (ct / t) x t
        // => 1 Day = 20 x rate^-1 = Minutes
        // => rate = 20 / Minutes
        Calendars.SERVER.setCalendarTickRate(dayLengthInMinutes == -1 ? 0f : 20f / dayLengthInMinutes);
        context.getSource().sendSuccess(() -> dayLengthInMinutes != -1
            ? Component.translatable("tfc.commands.time.set_day_length", dayLengthInMinutes)
            : Component.translatable("tfc.commands.time.set_day_length_disabled"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int setMonthLength(CommandContext<CommandSourceStack> context, int monthLengthInDays)
    {
        Calendars.SERVER.setMonthLength(monthLengthInDays);
        context.getSource().sendSuccess(() -> Component.translatable("tfc.commands.time.set_month_length", monthLengthInDays), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int setTimeFromDayTime(CommandContext<CommandSourceStack> context, float fractionOfDay)
    {
        final float currentFractionOfDay = Calendars.SERVER.getCalendarFractionOfDay();
        final float targetFraction = fractionOfDay > currentFractionOfDay ? fractionOfDay : 1 + fractionOfDay;
        return addTime(context, (long) ((targetFraction - currentFractionOfDay) * ICalendar.TICKS_IN_DAY));
    }

    private static int setTimeFromWeather(CommandContext<CommandSourceStack> context, boolean rain)
    {
        final CommandSourceStack source = context.getSource();
        final ClimateModel model = Climate.get(source.getLevel());
        final long calendarTick = Calendars.SERVER.getCalendarTicks();
        final float rainfall = model.getRainfall(source.getLevel(), BlockPos.containing(source.getPosition()));
        for (int tick = 0; tick < 400_000; tick += 1_000)
        {
            if (WeatherHelpers.isPrecipitating(model.getRain(calendarTick + tick), rainfall) == rain)
            {
                return addTime(context, tick);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addTime(CommandContext<CommandSourceStack> context, long ticksToAdd)
    {
        Calendars.SERVER.skipForwardBy(ticksToAdd);
        context.getSource().sendSuccess(() -> Component.translatable("tfc.commands.time.add_time", Calendars.SERVER.getTimeDelta(ticksToAdd), ticksToAdd), true);
        return Command.SINGLE_SUCCESS;
    }
}
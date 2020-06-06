/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.dries007.tfc.api.calendar.Calendar;
import net.dries007.tfc.api.calendar.ICalendar;

public class TFCTimeCommand
{
    private static final String DAYTIME = "tfc.commands.time.query.daytime";
    private static final String GAME_TIME = "tfc.commands.time.query.game_time";
    private static final String DAY = "tfc.commands.time.query.day";
    private static final String PLAYER_TICKS = "tfc.commands.time.query.player_ticks";
    private static final String CALENDAR_TICKS = "tfc.commands.time.query.calendar_ticks";

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        // First, remove the vanilla command by the same name
        // This seems to work. It does leave the command still lying around, but it shouldn't matter as we replace it anyway
        dispatcher.getRoot().getChildren().removeIf(node -> node.getName().equals("time"));
        dispatcher.register(Commands.literal("time")
            .requires(source -> source.hasPermissionLevel(2))
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
                        .executes(context -> addTime(IntegerArgumentType.getInteger(context, "value") * Calendar.CALENDAR_TIME.getTicksInYear()))
                    )
                )
                .then(Commands.literal("months")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(IntegerArgumentType.getInteger(context, "value") * Calendar.CALENDAR_TIME.getTicksInMonth()))
                    )
                )
                .then(Commands.literal("days")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(IntegerArgumentType.getInteger(context, "value") * ICalendar.TICKS_IN_DAY))
                    )
                )
                .then(Commands.literal("ticks")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(IntegerArgumentType.getInteger(context, "value")))
                    )
                )
            )
            .then(Commands.literal("query")
                .then(Commands.literal("daytime")
                    .executes(context -> sendQueryResults(context.getSource(), DAYTIME, Calendar.CALENDAR_TIME.getDayTime()))
                )
                .then(Commands.literal("gametime")
                    .executes(context -> sendQueryResults(context.getSource(), GAME_TIME, context.getSource().getWorld().getGameTime()))
                )
                .then(Commands.literal("day")
                    .executes(context -> sendQueryResults(context.getSource(), DAY, Calendar.CALENDAR_TIME.getTotalDays()))
                )
                .then(Commands.literal("playerticks")
                    .executes(context -> sendQueryResults(context.getSource(), PLAYER_TICKS, Calendar.PLAYER_TIME.getTicks()))
                )
                .then(Commands.literal("calendarticks")
                    .executes(context -> sendQueryResults(context.getSource(), CALENDAR_TICKS, Calendar.CALENDAR_TIME.getTicks()))
                )
            )
        );
    }

    private static int setMonthLength(int months)
    {
        Calendar.INSTANCE.setMonthLength(months);
        return Command.SINGLE_SUCCESS;
    }

    private static int setTime(MinecraftServer server, int dayTime)
    {
        for (World world : server.getWorlds())
        {
            long dayTimeJump = dayTime - (world.getDayTime() % ICalendar.TICKS_IN_DAY);
            if (dayTimeJump < 0)
            {
                dayTimeJump += ICalendar.TICKS_IN_DAY;
            }
            world.setDayTime(world.getDayTime() + dayTimeJump);
        }
        Calendar.INSTANCE.setTimeFromDayTime(dayTime);
        return Command.SINGLE_SUCCESS;
    }

    private static int addTime(long ticksToAdd)
    {
        Calendar.INSTANCE.setTimeFromCalendarTime(Calendar.CALENDAR_TIME.getTicks() + ticksToAdd);
        return Command.SINGLE_SUCCESS;
    }

    private static int sendQueryResults(CommandSource source, String translationKey, long value)
    {
        source.sendFeedback(new TranslationTextComponent(translationKey, (int) value), false);
        return Command.SINGLE_SUCCESS;
    }
}

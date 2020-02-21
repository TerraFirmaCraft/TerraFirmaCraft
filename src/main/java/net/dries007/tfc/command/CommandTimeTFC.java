/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CommandTimeTFC extends CommandBase
{
    @Override
    public String getName()
    {
        return "timetfc";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "tfc.command.time.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length >= 1)
        {
            if ("set".equals(args[0]))
            {
                if (args.length < 2)
                {
                    throw new WrongUsageException("tfc.command.time.usage_expected_second_argument_set");
                }
                else if ("monthlength".equals(args[1]))
                {
                    int newMonthLength = parseInt(args[2], 8, 128);
                    CalendarTFC.INSTANCE.setMonthLength(newMonthLength);
                    notifyCommandListener(sender, this, "tfc.command.time.set_month_length", newMonthLength);
                }
                else
                {
                    int resultWorldTime;
                    if ("day".equals(args[1]))
                    {
                        resultWorldTime = 1000;
                    }
                    else if ("night".equals(args[1]))
                    {
                        resultWorldTime = 13000;
                    }
                    else
                    {
                        resultWorldTime = parseInt(args[1], 0);
                    }
                    setAllWorldTimes(server, resultWorldTime);
                    CalendarTFC.INSTANCE.setTimeFromWorldTime(resultWorldTime);
                    notifyCommandListener(sender, this, "commands.time.set", resultWorldTime);
                }
            }
            else if ("add".equals(args[0]))
            {
                if (args.length < 2)
                {
                    throw new WrongUsageException("tfc.command.time.usage_expected_second_argument_add");
                }
                long timeToAdd;
                String unitAdded = args[1];
                switch (args[1])
                {
                    case "months":
                        timeToAdd = ICalendar.TICKS_IN_DAY * CalendarTFC.CALENDAR_TIME.getDaysInMonth() * parseInt(args[2], 0);
                        break;
                    case "years":
                        timeToAdd = ICalendar.TICKS_IN_DAY * CalendarTFC.CALENDAR_TIME.getDaysInMonth() * 12 * parseInt(args[2], 0);
                        break;
                    case "days":
                        timeToAdd = ICalendar.TICKS_IN_DAY * parseInt(args[2], 0);
                        break;
                    default:
                        unitAdded = "ticks";
                        timeToAdd = parseInt(args[1], 0);
                }
                long newCalendarTime = CalendarTFC.CALENDAR_TIME.getTicks() + timeToAdd;
                CalendarTFC.INSTANCE.setTimeFromCalendarTime(newCalendarTime);
                notifyCommandListener(sender, this, "tfc.command.time.added", timeToAdd, unitAdded);
            }
            else if ("query".equals(args[0]))
            {
                if (args.length < 2)
                {
                    throw new WrongUsageException("tfc.command.time.usage_expected_second_argument_query");
                }
                else if ("daytime".equals(args[1]))
                {
                    int daytime = (int) (sender.getEntityWorld().getWorldTime() % ICalendar.TICKS_IN_DAY);
                    sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, daytime);
                    notifyCommandListener(sender, this, "commands.time.query", "World Time", daytime);
                }
                else if ("day".equals(args[1]))
                {
                    int day = (int) (CalendarTFC.CALENDAR_TIME.getTotalDays() % Integer.MAX_VALUE);
                    sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, day);
                    notifyCommandListener(sender, this, "tfc.command.time.query", "Day Number (Calendar Time)", day);
                }
                else if ("gametime".equals(args[1]))
                {
                    int gameTime = (int) (sender.getEntityWorld().getTotalWorldTime() % Integer.MAX_VALUE);
                    sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, gameTime);
                    notifyCommandListener(sender, this, "tfc.command.time.query", "Total World Time (Game Time)", gameTime);
                }
                else if ("playerticks".equals(args[1]))
                {
                    int gameTime = (int) (CalendarTFC.PLAYER_TIME.getTicks() % Integer.MAX_VALUE);
                    sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, gameTime);
                    notifyCommandListener(sender, this, "tfc.command.time.query", "Player Ticks", gameTime);
                }
                else if ("calendarticks".equals(args[1]))
                {
                    int gameTime = (int) (CalendarTFC.CALENDAR_TIME.getTicks() % Integer.MAX_VALUE);
                    sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, gameTime);
                    notifyCommandListener(sender, this, "tfc.command.time.query", "Calendar Ticks", gameTime);
                }
                else
                {
                    throw new WrongUsageException("tfc.command.time.usage_expected_second_argument_query");
                }
            }
            else
            {
                throw new WrongUsageException("tfc.command.time.usage_expected_first_argument");
            }
        }
        else
        {
            throw new WrongUsageException("tfc.command.time.usage_expected_first_argument");
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "set", "add", "query");
        }
        else if (args.length == 2)
        {
            if ("set".equals(args[0]))
            {
                return getListOfStringsMatchingLastWord(args, "day", "night");
            }
            else if ("add".equals(args[0]))
            {
                return getListOfStringsMatchingLastWord(args, "months", "years", "days");
            }
            else if ("query".equals(args[0]))
            {
                return getListOfStringsMatchingLastWord(args, "daytime", "day", "gametime", "playertime", "calendartime", "date");
            }
        }
        return Collections.emptyList();
    }

    private void setAllWorldTimes(MinecraftServer server, long worldTime)
    {
        // Update the actual world times
        for (World world : server.worlds)
        {
            long worldTimeJump = worldTime - (world.getWorldTime() % ICalendar.TICKS_IN_DAY);
            if (worldTimeJump < 0)
            {
                worldTimeJump += ICalendar.TICKS_IN_DAY;
            }
            world.setWorldTime(world.getWorldTime() + worldTimeJump);
        }
    }
}

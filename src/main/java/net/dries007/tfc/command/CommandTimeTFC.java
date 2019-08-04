/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

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
        return "/timetfc <set|add> <<year|month|day|monthlength|playerticks> <value>|ticks <value|calendar_start>>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 3)
        {
            throw new WrongUsageException(getUsage(sender));
        }

        long time = ICalendar.TICKS_IN_DAY;
        boolean updateDaylightCycle = false;
        boolean isPlayerTime = false;
        switch (args[1].toLowerCase())
        {
            case "month":
            case "months":
                time *= CalendarTFC.INSTANCE.getDaysInMonth();
                time *= parseInt(args[2], 0, 12 * 1000);
                break;
            case "year":
            case "years":
                time *= CalendarTFC.INSTANCE.getDaysInMonth() * 12;
                time *= parseInt(args[2], 0, 1000);
                break;
            case "day":
            case "days":
                time *= parseInt(args[2], 0, CalendarTFC.INSTANCE.getDaysInMonth() * 12 * 1000);
                break;
            case "tick":
            case "ticks":
                // This one is different, because it needs to update the actual sun cycle
                if ("calendar_start".equals(args[2].toLowerCase()))
                {
                    time = CalendarTFC.DEFAULT_CALENDAR_TIME_OFFSET;
                }
                else
                {
                    time = parseInt(args[2], 0, Integer.MAX_VALUE);
                }
                updateDaylightCycle = true;
                break;
            case "playertick":
            case "playerticks":
                time = parseInt(args[2], 0, Integer.MAX_VALUE);
                isPlayerTime = true;
                break;
            case "monthlength":
                int value = parseInt(args[2], 1, 100);
                CalendarTFC.INSTANCE.setMonthLength(server.getEntityWorld(), value);
                sender.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.set_month_length", value));
                return;
            default:
                throw new WrongUsageException("Second argument must be <year|month|day|monthlength|playerticks|ticks>");
        }

        // Parse first argument
        if (args[0].equals("add"))
        {
            time += isPlayerTime ? CalendarTFC.PLAYER_TIME.getTicks() : CalendarTFC.CALENDAR_TIME.getTicks();
        }
        else if (args[0].equals("set"))
        {
            if (isPlayerTime)
            {
                throw new WrongUsageException("Player time cannot be set, only incremented");
            }
        }
        else
        {
            throw new WrongUsageException("First argument must be <add|set>");
        }

        // Update calendar
        if (isPlayerTime)
        {
            CalendarTFC.INSTANCE.setPlayerTime(server.getEntityWorld(), time);
            sender.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.time_command_set_player_time", time));
        }
        else
        {
            CalendarTFC.INSTANCE.setCalendarTime(server.getEntityWorld(), time);
            sender.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.time_command_set_calendar_time", CalendarTFC.CALENDAR_TIME.getTimeAndDate()));
        }

        if (updateDaylightCycle)
        {
            // Set world time (daylight cycle time)
            for (int i = 0; i < server.worlds.length; ++i)
            {
                long worldTime = time - CalendarTFC.WORLD_TIME_OFFSET;
                while (worldTime < 0)
                {
                    // Should only need to run once, but just to be safe
                    worldTime += ICalendar.TICKS_IN_DAY;
                }
                server.worlds[i].setWorldTime(worldTime);
            }
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
            return getListOfStringsMatchingLastWord(args, "set", "add");
        }
        else if (args.length == 2 && ("set".equals(args[0]) || "add".equals(args[0])))
        {
            return getListOfStringsMatchingLastWord(args, "year", "month", "day", "monthlength", "playerticks", "ticks");
        }
        else
        {
            return Collections.emptyList();
        }
    }
}

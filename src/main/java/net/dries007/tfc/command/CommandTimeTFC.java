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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.CalendarTFC;

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
        return "/timetfc <set|add> <year|month|day|monthLength|ticks> <value>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 3)
        {
            throw new WrongUsageException("Invalid arguments! /timetfc <set|add> <year|month|day|monthLength|ticks> <value>");
        }

        long time = CalendarTFC.TICKS_IN_DAY;
        boolean updateDaylightCycle = false;
        switch (args[1].toLowerCase())
        {
            case "month":
            case "months":
                time *= CalendarTFC.getDaysInMonth();
                time *= parseInt(args[2], 0, 12 * 1000);
                break;
            case "year":
            case "years":
                time *= CalendarTFC.getDaysInMonth() * 12;
                time *= parseInt(args[2], 0, 1000);
                break;
            case "day":
            case "days":
                time *= parseInt(args[2], 0, CalendarTFC.getDaysInMonth() * 12 * 1000);
                break;
            case "tick":
            case "ticks":
                // This one is different, because it needs to update the actual sun cycle
                time = parseInt(args[2], 0, Integer.MAX_VALUE);
                updateDaylightCycle = true;
                break;
            case "monthlength":
                int value = parseInt(args[2], 1, 1000);
                CalendarTFC.setMonthLength(server.getEntityWorld(), value);
                sender.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.set_month_length", value));
                return;
            default:
                throw new WrongUsageException("Second argument must be <day|month|year>");
        }

        if (args[0].equals("add"))
        {
            time += CalendarTFC.getCalendarTime();
        }
        else if (!args[0].equals("set"))
        {
            throw new WrongUsageException("First argument must be <add|set>");
        }

        CalendarTFC.setCalendarTime(server.getEntityWorld(), time);
        ITextComponent month = new TextComponentTranslation(Helpers.getEnumName(CalendarTFC.getMonthOfYear()));
        sender.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.set_time", CalendarTFC.getTotalYears(), month, CalendarTFC.getDayOfMonth(), String.format("%02d:%02d", CalendarTFC.getHourOfDay(), CalendarTFC.getMinuteOfHour())));

        if (updateDaylightCycle)
        {
            for (int i = 0; i < server.worlds.length; ++i)
            {
                server.worlds[i].setWorldTime(time);
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
            return getListOfStringsMatchingLastWord(args, "year", "month", "day", "monthlength", "ticks");
        }
        else
        {
            return Collections.emptyList();
        }
    }
}

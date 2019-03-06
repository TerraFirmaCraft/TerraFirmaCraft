package net.dries007.tfc.cmd;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.CalenderTFC;

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
        return "/timetfc [set|add] [year|month|day|monthLength] [value]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        TerraFirmaCraft.getLog().info("Executing command TFC");
        if (args.length != 3) throw new WrongUsageException("Requires three arguments");

        long time = CalenderTFC.TICKS_IN_DAY;
        switch (args[1].toLowerCase())
        {
            case "month":
                time *= CalenderTFC.getDaysInMonth();
                time *= parseInt(args[2], 0, 12 * 1000);
                break;
            case "year":
                time *= CalenderTFC.getDaysInMonth() * 12;
                time *= parseInt(args[2], 0, 1000);
                break;
            case "day":
                time *= parseInt(args[2], 0, CalenderTFC.getDaysInMonth() * 12 * 1000);
                break;
            case "monthlength":
                int value = parseInt(args[2], 1, 1000);
                CalenderTFC.setMonthLength(server.getEntityWorld(), value);
                sender.sendMessage(new TextComponentString("Set Month Length to " + value));
                return;
            default:
                throw new WrongUsageException("Second argument must be [day|month|year]");
        }

        if (args[0].equals("add"))
        {
            time += CalenderTFC.getCalendarTime();
        }
        else if (!args[0].equals("set"))
        {
            throw new WrongUsageException("First argument must be [add|set]");
        }

        CalenderTFC.setCalendarTime(server.getEntityWorld(), time);
        sender.sendMessage(new TextComponentString("Set Calendar Time to: " + time));
    }
}

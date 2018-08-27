/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.cmd;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;

public class ItemHeatCommand extends CommandBase
{
    @Override
    public String getName()
    {
        return "setHeat";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/setHeat <heat> -> Sets the current held item by the player to the heat value";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 1) throw new WrongUsageException("1 argument required.");
        double heat = parseDouble(args[0], 0, 1500);

        Entity e = sender.getCommandSenderEntity();
        if (e instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) e;
            ItemStack s = player.getHeldItemMainhand();
            IItemHeat h = s.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            if (h == null)
                throw new WrongUsageException("The held item in mainhand does not have the item heat capability");
            h.setTemperature((float) heat);
            s.setTagCompound(h.serializeNBT());
        }
        else
        {
            throw new WrongUsageException("Can only be used by a player");
        }
    }
}

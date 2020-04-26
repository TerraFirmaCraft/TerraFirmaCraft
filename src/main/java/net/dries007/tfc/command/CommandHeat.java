/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

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

@ParametersAreNonnullByDefault
public class CommandHeat extends CommandBase
{
    @Override
    @Nonnull
    public String getName()
    {
        return "heat";
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender)
    {
        return "tfc.command.heat.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 1) throw new WrongUsageException("tfc.command.heat.failed");
        double heat = parseDouble(args[0], 0);

        Entity entity = sender.getCommandSenderEntity();
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            ItemStack stack = player.getHeldItemMainhand();
            IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            if (cap == null)
                throw new WrongUsageException("tfc.command.heat.failed.missingcap");
            cap.setTemperature((float) heat);
        }
        else
        {
            throw new WrongUsageException("tfc.command.heat.failed.usage_expected_player");
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
}

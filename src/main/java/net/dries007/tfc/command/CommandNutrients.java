/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.nutrient.CapabilityFood;
import net.dries007.tfc.api.capability.nutrient.IPlayerNutrients;
import net.dries007.tfc.network.PacketPlayerNutrientsUpdate;
import net.dries007.tfc.util.agriculture.Nutrient;

@ParametersAreNonnullByDefault
public class CommandNutrients extends CommandBase
{
    @Override
    @Nonnull
    public String getName()
    {
        return "nutrients";
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender)
    {
        return "/nutrients <carbohydrates|fat|protein|minerals|vitamins> <value>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        // todo: make this use a player selector as the target (@a / @p, etc.)
        if (args.length != 2)
        {
            throw new WrongUsageException("Invalid arguments! /nutrients <carbohydrates|fat|protein|minerals|vitamins> <value>");
        }

        if (!(sender.getCommandSenderEntity() instanceof EntityPlayer))
        {
            throw new WrongUsageException("Can only be used by a player!");
        }

        try
        {
            Nutrient nutrient = Nutrient.valueOf(args[0].toUpperCase());
            IPlayerNutrients cap = sender.getCommandSenderEntity().getCapability(CapabilityFood.CAPABILITY_PLAYER_NUTRIENTS, null);
            if (cap != null)
            {
                float nutrientValue = (float) parseDouble(args[1]);
                cap.setNutrient(nutrient, nutrientValue);
                sender.sendMessage(new TextComponentString("Set Nutrients!"));
                if (sender.getCommandSenderEntity() instanceof EntityPlayerMP)
                {
                    TerraFirmaCraft.getNetwork().sendTo(new PacketPlayerNutrientsUpdate(cap), (EntityPlayerMP) sender.getCommandSenderEntity());
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new WrongUsageException("Unknown nutrient: " + args[0]);
        }
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "carbohydrates", "fat", "protein", "minerals", "vitamins");
        }
        else
        {
            return Collections.emptyList();
        }
    }
}

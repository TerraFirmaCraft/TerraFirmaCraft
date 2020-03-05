/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;

@ParametersAreNonnullByDefault
public class CommandVeinInfo extends CommandBase
{
    @Override
    @Nonnull
    public String getName()
    {
        return "veininfo";
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender)
    {
        return "/veininfo -> Show a list of all registered veins";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        sender.sendMessage(new TextComponentString("Registered Veins: "));
        String veins = "";
        for (String veinName : VeinRegistry.INSTANCE.keySet())
        {
            if (veins.isEmpty())
            {
                veins = veinName;
            }
            else
            {
                veins = veins.concat(", " + veinName);
            }
        }
        sender.sendMessage(new TextComponentString(veins));
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
}


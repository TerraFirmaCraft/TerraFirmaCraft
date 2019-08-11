/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.chunk.Chunk;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;

@ParametersAreNonnullByDefault
public class CommandFindOres extends CommandBase
{
    @Override
    @Nonnull
    public String getName()
    {
        return "findores";
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender)
    {
        return "/findores [all|<ore name>] <radius> -> Finds all generated instances of a specific ore, or all generated ores within a certain chunk radius";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 2) throw new WrongUsageException("2 arguments required.");
        if (sender.getCommandSenderEntity() == null) throw new WrongUsageException("Can only be used by a player");

        sender.sendMessage(new TextComponentString("Ores Found: "));

        final int radius = parseInt(args[1], 0, 50);
        final Map<Chunk, List<Ore>> chunkOres = Helpers.getChunkOres(sender.getEntityWorld(), sender.getCommandSenderEntity().chunkCoordX, sender.getCommandSenderEntity().chunkCoordZ, radius);
        if (!args[0].equals("all"))
        {
            final Ore ore = TFCRegistries.ORES.getValue(new ResourceLocation(args[0]));
            if (ore == null)
            {
                throw new WrongUsageException("Ore supplied does not match 'all' or any valid ore names. Use /oreinfo to see valid ore names");
            }
            // Search for veins matching type
            for (Chunk chunk : chunkOres.keySet())
            {
                chunkOres.get(chunk).removeIf(x -> x != ore);
            }
        }
        for (Chunk chunk : chunkOres.keySet())
        {
            chunkOres.get(chunk).forEach(x -> sender.sendMessage(new TextComponentString("> Ore: " + x.toString() + " at chunk X: " + (chunk.getPos().x) + " Z: " + (chunk.getPos().z))));
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, VeinRegistry.INSTANCE.keySet());
        }
        return Collections.emptyList();
    }
}


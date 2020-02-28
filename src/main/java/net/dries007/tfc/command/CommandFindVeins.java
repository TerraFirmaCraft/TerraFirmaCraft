/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

@ParametersAreNonnullByDefault
public class CommandFindVeins extends CommandBase
{
    public static Collection<Vein> getGeneratedVeins(World world, int chunkX, int chunkZ, int radius)
    {
        Set<Vein> veins = new HashSet<>();
        for (int x = chunkX - radius; x <= chunkX + radius; x++)
        {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++)
            {
                ChunkPos chunkPos = new ChunkPos(x, z);
                if (world.isBlockLoaded(chunkPos.getBlock(8, 0, 8)))
                {
                    Chunk chunk = world.getChunk(x, z);
                    ChunkDataTFC chunkData = ChunkDataTFC.get(chunk);
                    veins.addAll(chunkData.getGeneratedVeins());
                }
            }
        }
        return veins;
    }

    @Override
    @Nonnull
    public String getName()
    {
        return "findveins";
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender)
    {
        return "/findveins [all|<vein name>] <radius> -> Finds all instances of a specific vein, or all veins within a certain chunk radius";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 2) throw new WrongUsageException("2 arguments required.");
        if (sender.getCommandSenderEntity() == null) throw new WrongUsageException("Can only be used by a player");

        sender.sendMessage(new TextComponentString("Veins Found: "));

        final int radius = parseInt(args[1], 1, 1000);
        final Collection<Vein> veins = getGeneratedVeins(sender.getEntityWorld(), sender.getCommandSenderEntity().chunkCoordX, sender.getCommandSenderEntity().chunkCoordZ, radius);
        if (!args[0].equals("all"))
        {
            final VeinType type = VeinRegistry.INSTANCE.getVein(args[0]);
            if (type == null)
            {
                throw new WrongUsageException("Vein supplied does not match 'all' or any valid vein names. Use /veininfo to see valid vein names");
            }
            // Search for veins matching type
            veins.removeIf(x -> x.getType() != type);
        }
        veins.forEach(x -> sender.sendMessage(new TextComponentString("> Vein: " + x.getType() + " at " + x.getPos())));
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


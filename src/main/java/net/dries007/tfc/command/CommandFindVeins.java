/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

@ParametersAreNonnullByDefault
public class CommandFindVeins extends CommandBase
{
    public static Collection<Vein> getGeneratedVeins(World world, int chunkX, int chunkZ, int radius, boolean generated)
    {
        Set<Vein> veins = new HashSet<>();
        for (int x = chunkX - radius; x <= chunkX + radius; x++)
        {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++)
            {
                ChunkPos chunkPos = new ChunkPos(x, z);
                if (world.isBlockLoaded(chunkPos.getBlock(8, 0, 8)) || (generated && world.isChunkGeneratedAt(x, z)))
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
        return "/findveins [all|<vein name>] <radius> [dump|rate] -> Finds all instances of a specific vein, or all veins within a certain chunk radius, if dump or rate, also save it to a log file";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 2 && args.length != 3) throw new WrongUsageException("2 or 3 arguments required.");
        if (sender.getCommandSenderEntity() == null) throw new WrongUsageException("Can only be used by a player");
        int dumprate = 0;
        if (args.length == 3)
        {
            if (args[2].equalsIgnoreCase("dump"))
            {
                dumprate = 1;
            }
            else if (args[2].equalsIgnoreCase("rate"))
            {
                dumprate = 2;
            }
            else
            {
                throw new WrongUsageException("3rd argument must be dump or rate");
            }
        }
        final int radius = parseInt(args[1], 1, 1000);
        sender.sendMessage(new TextComponentString("Searching veins..."));
        final Collection<Vein> veins = getGeneratedVeins(sender.getEntityWorld(), sender.getCommandSenderEntity().chunkCoordX, sender.getCommandSenderEntity().chunkCoordZ, radius, dumprate > 0);
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
        sender.sendMessage(new TextComponentString("Veins Found: "));
        veins.forEach(x -> sender.sendMessage(new TextComponentString("> Vein: " + x.getType() + " at " + x.getPos())));
        if (dumprate == 1)
        {
            try
            {
                // Save files to MC's root folder
                String fileName = "tfc-veins-dump.log";
                File dumpFile = new File(fileName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(dumpFile));
                veins.forEach(x -> {
                    try
                    {
                        String dump = "Vein: " + x.getType() + " at " + x.getPos();
                        writer.write(dump);
                        writer.newLine();
                    }
                    catch (IOException e)
                    {
                        TerraFirmaCraft.getLog().error(e.getMessage());
                    }
                });
                writer.close();
                sender.sendMessage(new TextComponentString("vein dump file saved at: " + dumpFile.getAbsolutePath()));
            }
            catch (IOException e)
            {
                TerraFirmaCraft.getLog().error(e.getMessage());
            }
        }
        else if (dumprate == 2)
        {
            try
            {
                // Save files to MC's root folder
                String fileName = "tfc-veins-rate.log";
                File dumpFile = new File(fileName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(dumpFile));
                Map<VeinType, Integer> veinRateMap = new HashMap<>();
                veins.forEach(x -> {
                    int count = 1;
                    if (veinRateMap.containsKey(x.getType()))
                    {
                        count += veinRateMap.get(x.getType());
                    }
                    veinRateMap.put(x.getType(), count);
                });
                veinRateMap.forEach((veinType, count) -> {
                    try
                    {
                        String dump = "VeinType: " + veinType.getRegistryName() + " count: " + count;
                        writer.write(dump);
                        writer.newLine();
                    }
                    catch (IOException e)
                    {
                        TerraFirmaCraft.getLog().error(e.getMessage());
                    }
                });
                writer.close();
                sender.sendMessage(new TextComponentString("vein rate file saved at: " + dumpFile.getAbsolutePath()));
            }
            catch (IOException e)
            {
                TerraFirmaCraft.getLog().error(e.getMessage());
            }
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
        else if (args.length == 3)
        {
            return getListOfStringsMatchingLastWord(args, "dump", "rate");
        }
        return Collections.emptyList();
    }
}


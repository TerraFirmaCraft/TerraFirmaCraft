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
import java.util.function.Consumer;
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
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = MOD_ID)
public class CommandFindVeins extends CommandBase
{
    private static final List<ChunkPos> POSITIONS = new LinkedList<>();
    private static final Set<BlockPos> FOUND_VEINS = new HashSet<>(); // Using BlockPos instead of vein objs lowers ram usage

    private static Consumer<Vein> CONSUMER = null; // Runs for each found vein
    private static Consumer<Integer> LOGGER = null; // Tells someone how much job remains
    private static Runnable FINISHER = null; // Runs after everything finishes
    private static String FILTER = "all";

    /**
     * Doing that way so we spread chunk loading over ticks to not overload server too much
     */
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END && POSITIONS.size() > 0)
        {
            while (POSITIONS.size() > 0)
            {
                ChunkProviderServer chunkProvider = ((ChunkProviderServer) event.world.getChunkProvider());
                if (chunkProvider.loadedChunks.size() > ConfigTFC.GENERAL.findVeinsChunkLoad)
                {
                    return; // Breaks in the middle for performance
                }
                LOGGER.accept(POSITIONS.size());
                ChunkPos pos = POSITIONS.remove(0);
                Chunk chunk = event.world.getChunk(pos.getBlock(0, 0, 0));
                ChunkDataTFC chunkData = ChunkDataTFC.get(chunk);
                chunkData.getGeneratedVeins().stream()
                    .filter(vein -> !FOUND_VEINS.contains(vein.getPos()))
                    .filter(vein -> FILTER.equalsIgnoreCase("all") || vein.getType().getRegistryName().equalsIgnoreCase(FILTER))
                    .forEach(vein ->
                    {
                        FOUND_VEINS.add(vein.getPos());
                        CONSUMER.accept(vein);
                    });
                // Tell MC we don't need the chunk anymore
                chunkProvider.queueUnload(chunk);
            }
            FINISHER.run();

            // Resets everything
            POSITIONS.clear();
            FOUND_VEINS.clear();
            CONSUMER = null;
            LOGGER = null;
            FINISHER = null;
        }
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
        if (POSITIONS.size() > 0) throw new CommandException("Can't start another job while there's one running");
        // Default print to player chat
        Consumer<Vein> consumer = vein -> sender.sendMessage(new TextComponentString("> Vein: " + vein.getType() + " at " + vein.getPos()));
        Runnable finisher = () -> {}; // do nothing
        boolean generated = false;
        if (args.length >= 3)
        {
            generated = true;
            if (args[2].equalsIgnoreCase("dump"))
            {
                sender.sendMessage(new TextComponentString("Dumping veins, this is gonna take a while..."));
                try
                {
                    final String fileName = "tfc-veins-dump.log";
                    final File dumpFile = new File(fileName);
                    final BufferedWriter writer = new BufferedWriter(new FileWriter(dumpFile));
                    consumer = vein -> {
                        try
                        {
                            String dump = "Vein: " + vein.getType() + " at " + vein.getPos();
                            writer.write(dump);
                            writer.newLine();
                        }
                        catch (IOException e)
                        {
                            TerraFirmaCraft.getLog().error(e.getMessage());
                        }
                    };
                    finisher = () -> {
                        try
                        {
                            writer.close();
                            sender.sendMessage(new TextComponentString("vein dump file saved at: " + dumpFile.getAbsolutePath()));
                        }
                        catch (IOException e)
                        {
                            TerraFirmaCraft.getLog().error(e.getMessage());
                        }
                    };
                }
                catch (IOException e)
                {
                    TerraFirmaCraft.getLog().error(e.getMessage());
                    return;
                }
            }
            else if (args[2].equalsIgnoreCase("rate"))
            {
                sender.sendMessage(new TextComponentString("Dumping vein rates, this is gonna take a while..."));
                try
                {
                    // Save files to MC's root folder
                    String fileName = "tfc-veins-rate.log";
                    File dumpFile = new File(fileName);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(dumpFile));
                    Map<VeinType, Integer> veinRateMap = new HashMap<>();
                    consumer = vein -> {
                        int count = 1;
                        if (veinRateMap.containsKey(vein.getType()))
                        {
                            count += veinRateMap.get(vein.getType());
                        }
                        veinRateMap.put(vein.getType(), count);
                    };
                    finisher = () -> {
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
                        try
                        {
                            writer.close();
                            sender.sendMessage(new TextComponentString("vein rate file saved at: " + dumpFile.getAbsolutePath()));
                        }
                        catch (IOException e)
                        {
                            TerraFirmaCraft.getLog().error(e.getMessage());
                        }
                    };
                }
                catch (IOException e)
                {
                    TerraFirmaCraft.getLog().error(e.getMessage());
                }
            }
            else
            {
                throw new WrongUsageException("3rd argument must be dump or rate");
            }
        }

        final int radius = parseInt(args[1], 1, 1000);

        CONSUMER = consumer;
        FINISHER = finisher;
        FILTER = args[0];

        final int chunkX = sender.getCommandSenderEntity().chunkCoordX;
        final int chunkZ = sender.getCommandSenderEntity().chunkCoordZ;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, 0, 0);
        for (int x = chunkX - radius; x <= chunkX + radius; x++)
        {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++)
            {
                pos.setPos(x * 16, 0, z * 16);
                if (sender.getEntityWorld().isBlockLoaded(pos) || (generated && sender.getEntityWorld().isChunkGeneratedAt(x, z)))
                {
                    // Add to the list of positions so we spread chunk loading and not freeze / crash the server
                    POSITIONS.add(new ChunkPos(pos));
                }
            }
        }
        final int totalJob = POSITIONS.size();
        final int announceAt = totalJob / 20; // At each 5%
        if (generated)
        {
            LOGGER = remaining -> {
                if (remaining % announceAt == 0)
                {
                    sender.sendMessage(new TextComponentString("Chunks remaining: " + remaining + "/" + totalJob));
                }
            };
        }
        else
        {
            LOGGER = integer -> {}; // don't need to announce when we are dumping to chat already.
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


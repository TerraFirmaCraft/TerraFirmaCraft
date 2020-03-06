/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.command;

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
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.util.LogFileWriter;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = MOD_ID)
public class CommandFindVeins extends CommandBase
{
    private static final List<ChunkPos> POSITIONS = new LinkedList<>();

    private static Consumer<Chunk> CONSUMER = null; // Runs for each chunk
    private static Consumer<Integer> LOGGER = null; // Tells someone how much job remains
    private static Runnable FINISHER = null; // Runs after everything finishes

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
                CONSUMER.accept(chunk);
                // Tell MC we don't need the chunk anymore
                chunkProvider.queueUnload(chunk);
            }
            FINISHER.run();

            // Resets everything
            POSITIONS.clear();
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
        final Set<BlockPos> veinsFound = new HashSet<>(); // Using BlockPos instead of vein objs lowers ram usage
        final String filter = args[0];

        // Default print to player chat
        CONSUMER = chunk ->
        {
            ChunkDataTFC chunkData = ChunkDataTFC.get(chunk);
            chunkData.getGeneratedVeins().stream()
                .filter(vein -> !veinsFound.contains(vein.getPos()))
                .filter(vein -> filter.equalsIgnoreCase("all") || vein.getType().getRegistryName().equalsIgnoreCase(filter))
                .forEach(vein ->
                {
                    veinsFound.add(vein.getPos());
                    sender.sendMessage(new TextComponentString("> Vein: " + vein.getType() + " at " + vein.getPos()));
                });
        };
        FINISHER = () -> {}; // do nothing
        LOGGER = integer -> {}; // don't announce

        boolean generated = false;
        if (args.length >= 3)
        {
            generated = true;
            if (args[2].equalsIgnoreCase("dump"))
            {
                sender.sendMessage(new TextComponentString("Dumping veins, this is gonna take a while..."));
                final String fileName = "tfc-veins-dump.log";
                CONSUMER = chunk ->
                {
                    ChunkDataTFC chunkData = ChunkDataTFC.get(chunk);
                    chunkData.getGeneratedVeins().stream()
                        .filter(vein -> !veinsFound.contains(vein.getPos()))
                        .filter(vein -> filter.equalsIgnoreCase("all") || vein.getType().getRegistryName().equalsIgnoreCase(filter))
                        .forEach(vein ->
                        {
                            veinsFound.add(vein.getPos());
                            String dump = "Vein: " + vein.getType() + " at " + vein.getPos();
                            if (!LogFileWriter.isOpen())
                            {
                                LogFileWriter.open(fileName);
                            }
                            LogFileWriter.writeLine(dump);
                        });
                };
                FINISHER = () ->
                {
                    if (LogFileWriter.isOpen())
                    {
                        sender.sendMessage(new TextComponentString("vein dump file saved at: " + LogFileWriter.getFilePath()));
                        LogFileWriter.close();
                    }
                };
            }
            else if (args[2].equalsIgnoreCase("rate"))
            {
                sender.sendMessage(new TextComponentString("Dumping vein rates, this is gonna take a while..."));
                // Save files to MC's root folder
                String fileName = "tfc-veins-rate.log";
                Map<VeinType, Integer> veinRateMap = new HashMap<>();
                Map<Rock, Integer> rockRateMap = new HashMap<>();
                CONSUMER = chunk ->
                {
                    ChunkDataTFC chunkData = ChunkDataTFC.get(chunk);
                    Rock rock1 = chunkData.getRockLayer1(8, 8); // Grabbing the middle is fine
                    Rock rock2 = chunkData.getRockLayer1(8, 8);
                    Rock rock3 = chunkData.getRockLayer1(8, 8);

                    int value = 1;
                    if (rockRateMap.containsKey(rock1))
                    {
                        value += rockRateMap.get(rock1);
                    }
                    rockRateMap.put(rock1, value);

                    value = 1;
                    if (rockRateMap.containsKey(rock2))
                    {
                        value += rockRateMap.get(rock2);
                    }
                    rockRateMap.put(rock2, value);

                    value = 1;
                    if (rockRateMap.containsKey(rock3))
                    {
                        value += rockRateMap.get(rock3);
                    }
                    rockRateMap.put(rock3, value);

                    chunkData.getGeneratedVeins().stream()
                        .filter(vein -> !veinsFound.contains(vein.getPos()))
                        .filter(vein -> filter.equalsIgnoreCase("all") || vein.getType().getRegistryName().equalsIgnoreCase(filter))
                        .forEach(vein ->
                        {
                            veinsFound.add(vein.getPos());
                            int count = 1;
                            if (veinRateMap.containsKey(vein.getType()))
                            {
                                count += veinRateMap.get(vein.getType());
                            }
                            veinRateMap.put(vein.getType(), count);
                        });
                };
                FINISHER = () ->
                {
                    if (!LogFileWriter.isOpen())
                    {
                        LogFileWriter.open(fileName);
                    }

                    veinRateMap.forEach((veinType, count) -> LogFileWriter.writeLine("VeinType: " + veinType.getRegistryName() + " count: " + count));

                    LogFileWriter.newLine();
                    LogFileWriter.writeLine("Found Rock Layers: ");

                    rockRateMap.forEach((rock, count) -> LogFileWriter.writeLine("Rock: " + rock.getRegistryName() + " chunks: " + count));

                    sender.sendMessage(new TextComponentString("vein rate file saved at: " + LogFileWriter.getFilePath()));
                    LogFileWriter.close();
                };
            }
            else
            {
                throw new WrongUsageException("3rd argument must be dump or rate");
            }
        }

        final int radius = parseInt(args[1], 1, 1000);
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
            LOGGER = remaining ->
            {
                if (remaining % announceAt == 0)
                {
                    sender.sendMessage(new TextComponentString("Chunks remaining: " + remaining + "/" + totalJob));
                }
            };
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


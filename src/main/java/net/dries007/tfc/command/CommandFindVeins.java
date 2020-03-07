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
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.WorldWorkerManager;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.util.LogFileWriter;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

@ParametersAreNonnullByDefault
public class CommandFindVeins extends CommandBase
{
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
        return "/findveins [all|<vein name>] <radius> [dump|rate] -> Finds all instances of a specific vein, or all veins within a certain chunk radius, if dump or rate save it to a log file";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 2 && args.length != 3) throw new WrongUsageException("2 or 3 arguments required.");
        if (sender.getCommandSenderEntity() == null) throw new WrongUsageException("Can only be used by a player");

        final List<ChunkPos> chunks = new LinkedList<>();
        final Set<BlockPos> veinsFound = new HashSet<>(); // Using BlockPos instead of vein objs lowers ram usage
        final String filter = args[0];

        // Default print to player chat
        Consumer<Chunk> consumer = chunk ->
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
        Consumer<Integer> logger = integer -> {}; // don't announce
        Runnable finisher = () -> {}; // do nothing

        boolean generated = false;
        if (args.length >= 3)
        {
            generated = true;
            if (args[2].equalsIgnoreCase("dump"))
            {
                sender.sendMessage(new TextComponentString("Dumping veins, this is gonna take a while..."));
                final String fileName = "tfc-veins-dump.log";
                consumer = chunk ->
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
                finisher = () ->
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
                consumer = chunk ->
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
                finisher = () ->
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
                    chunks.add(new ChunkPos(pos));
                }
            }
        }
        final int totalJob = chunks.size();
        if (generated)
        {
            logger = remaining -> sender.sendMessage(new TextComponentString("Chunks remaining: " + remaining + "/" + totalJob));
        }
        WorldWorkerManager.IWorker worker = new Worker(0, sender, chunks, consumer, logger, finisher);
        WorldWorkerManager.addWorker(worker);
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

    private static class Worker implements WorldWorkerManager.IWorker
    {
        private final int dimension;
        private final ICommandSender listener;
        private final List<ChunkPos> chunks;
        private final Consumer<Chunk> consumer; // Runs for each chunk
        private final Consumer<Integer> logger; // Tells someone how much job remains
        private final Runnable finisher; // Runs after everything finishes

        private long lastNotifcationTime;
        private Boolean keepingLoaded;

        public Worker(int dimension, ICommandSender listener, List<ChunkPos> chunks, Consumer<Chunk> consumer, Consumer<Integer> logger, Runnable finisher)
        {
            this.dimension = dimension;
            this.listener = listener;
            this.chunks = chunks;
            this.consumer = consumer;
            this.logger = logger;
            this.finisher = finisher;
            lastNotifcationTime = 0;
            keepingLoaded = false;
        }

        @Override
        public boolean hasWork()
        {
            return chunks.size() > 0;
        }

        @Override
        public boolean doWork()
        {
            WorldServer world = DimensionManager.getWorld(dimension);
            if (world == null)
            {
                DimensionManager.initDimension(dimension);
                world = DimensionManager.getWorld(dimension);
                if (world == null)
                {
                    listener.sendMessage(new TextComponentString("Failed to load dimension " + dimension));
                    chunks.clear();
                    return false;
                }
            }

            AnvilChunkLoader loader = world.getChunkProvider().chunkLoader instanceof AnvilChunkLoader ? (AnvilChunkLoader) world.getChunkProvider().chunkLoader : null;
            if (loader != null && loader.getPendingSaveCount() > 100)
            {
                // if this block is called, that's because chunk saving is lagging, not much we can do besides waiting
                // Slowing down notification to not spam the same value too much
                if (lastNotifcationTime < System.currentTimeMillis() - 10000) // 10 sec notification
                {
                    logger.accept(chunks.size());
                    lastNotifcationTime = System.currentTimeMillis();
                }
                return false;
            }

            ChunkPos next = chunks.remove(0);

            if (next != null)
            {
                if (lastNotifcationTime < System.currentTimeMillis() - 5000) // 5 sec notification
                {
                    logger.accept(chunks.size());
                    lastNotifcationTime = System.currentTimeMillis();
                }

                // While we work we don't want to cause world load spam so pause unloading the world.
                if (!keepingLoaded)
                {
                    keepingLoaded = DimensionManager.keepDimensionLoaded(dimension, true);
                }

                Chunk target = world.getChunk(next.x, next.z);

                consumer.accept(target);

                PlayerChunkMapEntry watchers = world.getPlayerChunkMap().getEntry(target.x, target.z);
                if (watchers == null) //If there are no players watching this, this will be null, so we can unload.
                {
                    world.getChunkProvider().queueUnload(target);
                }
            }

            if (chunks.isEmpty())
            {
                finisher.run();
                if (keepingLoaded)
                {
                    DimensionManager.keepDimensionLoaded(dimension, false);
                }
                return false;
            }
            return true;
        }
    }
}


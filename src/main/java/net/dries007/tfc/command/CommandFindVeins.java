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
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.WorldWorkerManager;

import gnu.trove.map.hash.TObjectIntHashMap;
import net.dries007.tfc.api.types.Rock;
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
        return "tfc.command.findveins.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (sender.getCommandSenderEntity() == null)
            throw new WrongUsageException("tfc.command.findveins.usage_expected_player");
        if (args.length != 2 && args.length != 3) throw new WrongUsageException("tfc.command.findveins.usage");

        VeinType filter;
        if ("all".equals(args[0]))
        {
            filter = null;
        }
        else
        {
            filter = VeinRegistry.INSTANCE.getVein(args[0]);
            if (filter == null)
            {
                throw new WrongUsageException("tfc.command.findveins.usage_first_argument_not_vein", args[0]);
            }
        }

        final List<ChunkPos> chunks = new LinkedList<>();

        int type = 0;
        boolean generated = false;
        if (args.length >= 3)
        {
            generated = true;
            if (args[2].equalsIgnoreCase("dump"))
            {
                type = 1;
                sender.sendMessage(new TextComponentTranslation("tfc.command.findveins.dump_veins"));
            }
            else if (args[2].equalsIgnoreCase("rate"))
            {
                type = 2;
                sender.sendMessage(new TextComponentTranslation("tfc.command.findveins.rate_veins"));
            }
            else
            {
                throw new WrongUsageException("tfc.command.findveins.usage");
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
        WorldWorkerManager.IWorker worker = new Worker(sender, chunks, filter, type);
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
        private static final int DIMENSION = 0; // In TFC, veins can only be generated in dimension 0. Should this change in the future, please update this accordingly
        private final ICommandSender listener;
        private final List<ChunkPos> chunks;
        private final int jobSize;
        private final VeinType filter; // null if any, a vein type to filter out all other veins

        private final Set<BlockPos> veinsFound = new HashSet<>(); // Using BlockPos instead of vein objs lowers ram usage

        // Selects which type of worker this one is. Since they have so many similarities, I don't see a point in creating other classes and copy pasting code
        // 0 = output found veins in chat
        // 1 = output all vein instances in log file
        // 2 = count vein types and rock types (effort to close #867)
        private final int type;

        // Only used in type = 1
        private final List<String> outputLog = new ArrayList<>();

        // Only used in type = 2
        private final TObjectIntHashMap<VeinType> veinRateMap = new TObjectIntHashMap<>();
        private final TObjectIntHashMap<Rock> rockRateMap = new TObjectIntHashMap<>();


        private long lastNotifcationTime;
        private Boolean keepingLoaded;

        public Worker(@Nonnull ICommandSender listener, @Nonnull List<ChunkPos> chunks, @Nullable VeinType filter, int type)
        {
            this.listener = listener;
            this.chunks = chunks;
            this.jobSize = chunks.size();
            this.type = type;
            this.filter = filter;
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
            WorldServer world = DimensionManager.getWorld(DIMENSION);
            if (world == null)
            {
                DimensionManager.initDimension(DIMENSION);
                world = DimensionManager.getWorld(DIMENSION);
                if (world == null)
                {
                    listener.sendMessage(new TextComponentTranslation("tfc.command.findveins.failed", DIMENSION));
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
                    if (type > 0)
                    {
                        this.listener.sendMessage(new TextComponentTranslation("tfc.command.findveins.remaining_chunks", chunks.size(), jobSize));
                    }
                    lastNotifcationTime = System.currentTimeMillis();
                }
                return false;
            }

            ChunkPos next = chunks.remove(0);

            if (next != null)
            {
                if (lastNotifcationTime < System.currentTimeMillis() - 5000) // 5 sec notification
                {
                    if (type > 0)
                    {
                        this.listener.sendMessage(new TextComponentTranslation("tfc.command.findveins.remaining_chunks", chunks.size(), jobSize));
                    }
                    lastNotifcationTime = System.currentTimeMillis();
                }

                // While we work we don't want to cause world load spam so pause unloading the world.
                if (!keepingLoaded)
                {
                    keepingLoaded = DimensionManager.keepDimensionLoaded(DIMENSION, true);
                }

                Chunk target = world.getChunk(next.x, next.z);
                ChunkDataTFC chunkData = ChunkDataTFC.get(target);

                chunkData.getGeneratedVeins().stream()
                    .filter(vein -> !veinsFound.contains(vein.getPos()))
                    .filter(vein -> filter == null || filter.equals(vein.getType()))
                    .forEach(vein ->
                    {
                        veinsFound.add(vein.getPos());
                        String veinName = "Unregistered Vein";
                        if (vein.getType() != null)
                        {
                            veinName = vein.getType().getRegistryName();
                        }
                        if (type == 0)
                        {
                            listener.sendMessage(new TextComponentTranslation("tfc.command.findveins.output", veinName, vein.getPos()));
                        }
                        else if (type == 1)
                        {
                            outputLog.add(String.format("Found %s at %s", veinName, vein.getPos()));
                        }
                        else if (type == 2 && vein.getType() != null)
                        {
                            int count = 1;
                            if (veinRateMap.containsKey(vein.getType()))
                            {
                                count += veinRateMap.get(vein.getType());
                            }
                            veinRateMap.put(vein.getType(), count);
                        }
                    });

                if (type == 2)
                {
                    // Also count rock layers
                    Rock rock1 = chunkData.getRockLayer1(8, 8); // Grabbing the middle is fine
                    Rock rock2 = chunkData.getRockLayer2(8, 8);
                    Rock rock3 = chunkData.getRockLayer3(8, 8);

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
                }

                PlayerChunkMapEntry watchers = world.getPlayerChunkMap().getEntry(target.x, target.z);
                if (watchers == null) //If there are no players watching this, this will be null, so we can unload.
                {
                    world.getChunkProvider().queueUnload(target);
                }
            }

            if (chunks.isEmpty())
            {
                if (type == 1)
                {
                    final String fileName = "tfc-veins-dump.log";
                    final File file = new File(fileName);
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
                    {
                        for (String line : outputLog)
                        {
                            writer.write(line);
                            writer.newLine();
                        }

                        listener.sendMessage(new TextComponentTranslation("tfc.command.findveins.output_file", file.getAbsolutePath()));
                    }
                    catch (IOException error)
                    {
                        listener.sendMessage(new TextComponentTranslation("tfc.command.findveins.output_file.error", error.toString()));
                    }

                }
                else if (type == 2)
                {
                    final String fileName = "tfc-veins-rate.log";
                    final File file = new File(fileName);
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
                    {
                        writer.write("Found Veins: ");
                        writer.newLine();
                        for (VeinType veinType : veinRateMap.keySet())
                        {
                            String line = String.format("%s: %d", veinType.getRegistryName(), veinRateMap.get(veinType));
                            writer.write(line);
                            writer.newLine();
                        }

                        writer.newLine();
                        writer.write("Found Rock Layers (chunks): ");
                        writer.newLine();

                        for (Rock rock : rockRateMap.keySet())
                        {
                            String line = String.format("%s: %d", rock, rockRateMap.get(rock));
                            writer.write(line);
                            writer.newLine();
                        }

                        listener.sendMessage(new TextComponentTranslation("tfc.command.findveins.output_file", file.getAbsolutePath()));
                    }
                    catch (IOException error)
                    {
                        listener.sendMessage(new TextComponentTranslation("tfc.command.findveins.output_file.error", error.toString()));
                    }
                }
                if (keepingLoaded)
                {
                    DimensionManager.keepDimensionLoaded(DIMENSION, false);
                }
                return false;
            }
            return true;
        }
    }
}


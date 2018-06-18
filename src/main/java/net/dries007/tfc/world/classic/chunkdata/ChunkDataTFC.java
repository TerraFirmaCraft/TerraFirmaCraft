/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.chunkdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.util.OreSpawnData;
import net.dries007.tfc.world.classic.DataLayer;

import static net.dries007.tfc.world.classic.WorldTypeTFC.ROCKLAYER2;
import static net.dries007.tfc.world.classic.WorldTypeTFC.ROCKLAYER3;

@SuppressWarnings("WeakerAccess")
public final class ChunkDataTFC
{
    public static final int FISH_POP_MAX = 60;

    private static final ChunkDataTFC EMPTY = new ChunkDataTFC();

    static
    {
        Arrays.fill(EMPTY.rockLayer1, DataLayer.ERROR);
        Arrays.fill(EMPTY.rockLayer2, DataLayer.ERROR);
        Arrays.fill(EMPTY.rockLayer3, DataLayer.ERROR);
        Arrays.fill(EMPTY.evtLayer, DataLayer.ERROR);
        Arrays.fill(EMPTY.rainfallLayer, DataLayer.ERROR);
        Arrays.fill(EMPTY.drainageLayer, DataLayer.ERROR);
        Arrays.fill(EMPTY.stabilityLayer, DataLayer.ERROR);
        Arrays.fill(EMPTY.seaLevelOffset, -1);
    }

    public static ChunkDataTFC get(World world, BlockPos pos)
    {
        ChunkDataTFC data = world.getChunkFromBlockCoords(pos).getCapability(ChunkDataProvider.CHUNK_DATA_CAPABILITY, null);
        return data == null ? EMPTY : data;
    }

    public static BlockRockVariant getRock1(World world, BlockPos pos) { return get(world, pos).getRockLayer1(pos.getX() & 15, pos.getZ() & 15).block; }

    public static BlockRockVariant getRock2(World world, BlockPos pos) { return get(world, pos).getRockLayer2(pos.getX() & 15, pos.getZ() & 15).block; }

    public static BlockRockVariant getRock3(World world, BlockPos pos) { return get(world, pos).getRockLayer3(pos.getX() & 15, pos.getZ() & 15).block; }

    public static float getEvt(World world, BlockPos pos) { return get(world, pos).getEvtLayer(pos.getX() & 15, pos.getZ() & 15).valueFloat; }

    public static float getRainfall(World world, BlockPos pos) { return get(world, pos).getRainfallLayer(pos.getX() & 15, pos.getZ() & 15).valueFloat; }

    public static boolean isStable(World world, BlockPos pos) { return get(world, pos).getStabilityLayer(pos.getX() & 15, pos.getZ() & 15).valueInt == 0; }

    public static int getDrainage(World world, BlockPos pos) { return get(world, pos).getDrainageLayer(pos.getX() & 15, pos.getZ() & 15).valueInt; }

    public static int getSeaLevelOffset(World world, BlockPos pos) { return get(world, pos).getSeaLevelOffset(pos.getX() & 15, pos.getZ() & 15); }

    public static int getFishPopulation(World world, BlockPos pos) { return get(world, pos).getFishPopulation(); }

    public static BlockRockVariant getRockHeight(World world, BlockPos pos) { return get(world, pos).getRockLayerHeight(pos.getX() & 15, pos.getY(), pos.getZ() & 15).block; }

    private final DataLayer[] rockLayer1 = new DataLayer[256];
    private final DataLayer[] rockLayer2 = new DataLayer[256];
    private final DataLayer[] rockLayer3 = new DataLayer[256];
    private final DataLayer[] evtLayer = new DataLayer[256];
    private final DataLayer[] rainfallLayer = new DataLayer[256];
    private final DataLayer[] drainageLayer = new DataLayer[256];
    private final DataLayer[] stabilityLayer = new DataLayer[256];
    private final int[] seaLevelOffset = new int[256];
    private final List<ChunkDataOreSpawned> oresSpawned = new ArrayList<>();
    private final List<ChunkDataOreSpawned> oresSpawnedView = Collections.unmodifiableList(oresSpawned);
    private boolean initialized = false;
    private int fishPopulation = FISH_POP_MAX; // todo: Set this based on biome? temp? rng?

    /**
     * INTERNAL USE ONLY.
     * No need to mark as dirty, since this will only ever be called on worldgen, before the first chunk save.
     */
    public void setGenerationData(DataLayer[] rockLayer1, DataLayer[] rockLayer2, DataLayer[] rockLayer3, DataLayer[] evtLayer, DataLayer[] rainfallLayer, DataLayer[] stabilityLayer, DataLayer[] drainageLayer, int[] seaLevelOffset)
    {
        this.initialized = true;
        System.arraycopy(rockLayer1, 0, this.rockLayer1, 0, 256);
        System.arraycopy(rockLayer2, 0, this.rockLayer2, 0, 256);
        System.arraycopy(rockLayer3, 0, this.rockLayer3, 0, 256);
        System.arraycopy(evtLayer, 0, this.evtLayer, 0, 256);
        System.arraycopy(rainfallLayer, 0, this.rainfallLayer, 0, 256);
        System.arraycopy(stabilityLayer, 0, this.stabilityLayer, 0, 256);
        System.arraycopy(drainageLayer, 0, this.drainageLayer, 0, 256);
        System.arraycopy(seaLevelOffset, 0, this.seaLevelOffset, 0, 256);
    }

    /**
     * INTERNAL USE ONLY.
     */
    public void addSpawnedOre(Ore ore, IBlockState state, OreSpawnData.SpawnSize size, Ore.Grade grade, BlockPos pos, int count)
    {
        oresSpawned.add(new ChunkDataOreSpawned(ore, state, size, grade, pos, count));
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    public BlockRockVariant getRock1(BlockPos pos) { return getRock1(pos.getX() & 15, pos.getY() & 15); }
    public BlockRockVariant getRock1(int x, int z) { return getRockLayer1(x, z).block; }

    public BlockRockVariant getRock2(BlockPos pos) { return getRock2(pos.getX() & 15, pos.getY() & 15); }
    public BlockRockVariant getRock2(int x, int z) { return getRockLayer2(x, z).block; }

    public BlockRockVariant getRock3(BlockPos pos) { return getRock3(pos.getX() & 15, pos.getY() & 15); }
    public BlockRockVariant getRock3(int x, int z) { return getRockLayer3(x, z).block; }

    public float getEvt(int x, int z) { return getEvtLayer(x, z).valueFloat; }

    public float getRainfall(int x, int z) { return getRainfallLayer(x, z).valueFloat; }

    public boolean isStable(int x, int z) { return getStabilityLayer(x, z).valueInt == 0; }

    public int getDrainage(int x, int z) { return getDrainageLayer(x, z).valueInt; }

    public BlockRockVariant getRockHeight(BlockPos pos) { return getRockHeight(pos.getX(), pos.getY(), pos.getZ()); }
    public BlockRockVariant getRockHeight(int x, int y, int z) { return getRockLayerHeight(x & 15, y, z & 15).block; }

    public int getSeaLevelOffset(BlockPos pos) { return getSeaLevelOffset(pos.getX() & 15, pos.getY() & 15); }
    public int getSeaLevelOffset(int x, int z) { return seaLevelOffset[z << 4 | x]; }

    public int getFishPopulation() { return fishPopulation; }

    public List<ChunkDataOreSpawned> getOresSpawned() { return oresSpawnedView; }

    // Directly accessing the DataLayer is discouraged (except for getting the name). It's easy to use the wrong value.
    public DataLayer getRockLayer1(int x, int z) { return rockLayer1[z << 4 | x]; }

    public DataLayer getRockLayer2(int x, int z) { return rockLayer2[z << 4 | x]; }

    public DataLayer getRockLayer3(int x, int z) { return rockLayer3[z << 4 | x]; }

    public DataLayer getEvtLayer(int x, int z) { return evtLayer[z << 4 | x]; }

    public DataLayer getRainfallLayer(int x, int z) { return rainfallLayer[z << 4 | x]; }

    public DataLayer getStabilityLayer(int x, int z) { return stabilityLayer[z << 4 | x]; }

    public DataLayer getDrainageLayer(int x, int z) { return drainageLayer[z << 4 | x]; }

    public DataLayer getRockLayerHeight(int x, int y, int z)
    {
        int offset = getSeaLevelOffset(x, z);
        if (y <= ROCKLAYER3 + offset) return getRockLayer3(x, z);
        if (y <= ROCKLAYER2 + offset) return getRockLayer2(x, z);
        return getRockLayer1(x, z);
    }

    public static final class ChunkDataStorage implements Capability.IStorage<ChunkDataTFC>
    {
        public static NBTTagByteArray write(DataLayer[] layers)
        {
            return new NBTTagByteArray(Arrays.stream(layers).map(x -> (byte) x.layerID).collect(Collectors.toList()));
        }

        public static void read(DataLayer[] layers, byte[] bytes)
        {
            for (int i = bytes.length - 1; i >= 0; i--)
            {
                layers[i] = DataLayer.get(bytes[i]);
            }
        }

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<ChunkDataTFC> capability, ChunkDataTFC instance, EnumFacing side)
        {
            if (instance == null) return null;
            NBTTagCompound root = new NBTTagCompound();
            root.setTag("rockLayer1", write(instance.rockLayer1));
            root.setTag("rockLayer2", write(instance.rockLayer2));
            root.setTag("rockLayer3", write(instance.rockLayer3));
            root.setTag("evtLayer", write(instance.evtLayer));
            root.setTag("rainfallLayer", write(instance.rainfallLayer));
            root.setTag("stabilityLayer", write(instance.stabilityLayer));
            root.setTag("drainageLayer", write(instance.drainageLayer));

            root.setTag("seaLevelOffset", new NBTTagIntArray(instance.seaLevelOffset));
            root.setInteger("fishPopulation", instance.fishPopulation);

            NBTTagList chunkDataOreSpawnedNBT = new NBTTagList();
            instance.oresSpawned.stream().map(ChunkDataOreSpawned::serialize).forEach(chunkDataOreSpawnedNBT::appendTag);
            root.setTag("oresSpawned", chunkDataOreSpawnedNBT);

            return root;
        }

        @Override
        public void readNBT(Capability<ChunkDataTFC> capability, ChunkDataTFC instance, EnumFacing side, NBTBase nbt)
        {
            NBTTagCompound root = ((NBTTagCompound) nbt);
            read(instance.rockLayer1, root.getByteArray("rockLayer1"));
            read(instance.rockLayer2, root.getByteArray("rockLayer2"));
            read(instance.rockLayer3, root.getByteArray("rockLayer3"));
            read(instance.evtLayer, root.getByteArray("evtLayer"));
            read(instance.rainfallLayer, root.getByteArray("rainfallLayer"));
            read(instance.stabilityLayer, root.getByteArray("stabilityLayer"));
            read(instance.drainageLayer, root.getByteArray("drainageLayer"));

            System.arraycopy(root.getIntArray("seaLevelOffset"), 0, instance.seaLevelOffset, 0, 256);
            instance.fishPopulation = root.getInteger("fishPopulation");

            instance.oresSpawned.clear();
            root.getTagList("oresSpawned", Constants.NBT.TAG_COMPOUND).forEach(x -> instance.oresSpawned.add(new ChunkDataOreSpawned(((NBTTagCompound) x))));

            instance.initialized = true;
        }
    }
}

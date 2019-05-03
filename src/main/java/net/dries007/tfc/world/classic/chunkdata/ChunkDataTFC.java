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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.util.NBTBuilder;
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
        Arrays.fill(EMPTY.drainageLayer, DataLayer.ERROR);
        Arrays.fill(EMPTY.stabilityLayer, DataLayer.ERROR);
        Arrays.fill(EMPTY.seaLevelOffset, -1);
    }

    @Nonnull
    public static ChunkDataTFC get(World world, BlockPos pos)
    {
        return get(world.getChunk(pos));
    }

    @Nonnull
    public static ChunkDataTFC get(Chunk chunk)
    {
        ChunkDataTFC data = chunk.getCapability(ChunkDataProvider.CHUNK_DATA_CAPABILITY, null);
        return data == null ? EMPTY : data;
    }

    public static Rock getRock1(World world, BlockPos pos) { return get(world, pos).getRockLayer1(pos.getX() & 15, pos.getZ() & 15); }

    public static Rock getRock2(World world, BlockPos pos) { return get(world, pos).getRockLayer2(pos.getX() & 15, pos.getZ() & 15); }

    public static Rock getRock3(World world, BlockPos pos) { return get(world, pos).getRockLayer3(pos.getX() & 15, pos.getZ() & 15); }

    public static float getRainfall(World world, BlockPos pos) { return get(world, pos).getRainfall(); }

    public static boolean isStable(World world, BlockPos pos) { return get(world, pos).getStabilityLayer(pos.getX() & 15, pos.getZ() & 15).valueInt == 0; }

    public static int getDrainage(World world, BlockPos pos) { return get(world, pos).getDrainageLayer(pos.getX() & 15, pos.getZ() & 15).valueInt; }

    public static int getSeaLevelOffset(World world, BlockPos pos) { return get(world, pos).getSeaLevelOffset(pos.getX() & 15, pos.getZ() & 15); }

    public static int getFishPopulation(World world, BlockPos pos) { return get(world, pos).getFishPopulation(); }

    public static Rock getRockHeight(World world, BlockPos pos) { return get(world, pos).getRockLayerHeight(pos.getX() & 15, pos.getY(), pos.getZ() & 15); }

    private final int[] rockLayer1 = new int[256];
    private final int[] rockLayer2 = new int[256];
    private final int[] rockLayer3 = new int[256];
    private final DataLayer[] drainageLayer = new DataLayer[256]; // To be removed / replaced?
    private final DataLayer[] stabilityLayer = new DataLayer[256]; // To be removed / replaced?
    private final int[] seaLevelOffset = new int[256];
    private final List<ChunkDataOreSpawned> oresSpawned = new ArrayList<>();
    private final List<ChunkDataOreSpawned> oresSpawnedView = Collections.unmodifiableList(oresSpawned);
    private boolean initialized = false;
    private int fishPopulation = FISH_POP_MAX; // todo: Set this based on biome? temp? rng?

    private float rainfall;
    private float baseTemp;
    private float avgTemp;
    private float floraDensity;
    private float floraDiversity;

    /**
     * INTERNAL USE ONLY.
     * No need to mark as dirty, since this will only ever be called on worldgen, before the first chunk save.
     */
    public void setGenerationData(int[] rockLayer1, int[] rockLayer2, int[] rockLayer3, DataLayer[] stabilityLayer, DataLayer[] drainageLayer, int[] seaLevelOffset,
                                  float rainfall, float baseTemp, float avgTemp, float floraDensity, float floraDiversity)
    {
        this.initialized = true;
        System.arraycopy(rockLayer1, 0, this.rockLayer1, 0, 256);
        System.arraycopy(rockLayer2, 0, this.rockLayer2, 0, 256);
        System.arraycopy(rockLayer3, 0, this.rockLayer3, 0, 256);
        System.arraycopy(stabilityLayer, 0, this.stabilityLayer, 0, 256);
        System.arraycopy(drainageLayer, 0, this.drainageLayer, 0, 256);
        System.arraycopy(seaLevelOffset, 0, this.seaLevelOffset, 0, 256);

        this.rainfall = rainfall;
        this.baseTemp = baseTemp;
        this.avgTemp = avgTemp;
        this.floraDensity = floraDensity;
        this.floraDiversity = floraDiversity;
    }

    /**
     * INTERNAL USE ONLY.
     */
    public void addSpawnedOre(Ore ore, OreSpawnData.SpawnSize size, Ore.Grade grade, BlockPos pos)
    {
        oresSpawned.add(new ChunkDataOreSpawned(ore, size, grade, pos));
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    public Rock getRock1(BlockPos pos) { return getRock1(pos.getX() & 15, pos.getY() & 15); }

    public Rock getRock1(int x, int z) { return getRockLayer1(x, z); }

    public Rock getRock2(BlockPos pos) { return getRock2(pos.getX() & 15, pos.getY() & 15); }

    public Rock getRock2(int x, int z) { return getRockLayer2(x, z); }

    public Rock getRock3(BlockPos pos) { return getRock3(pos.getX() & 15, pos.getY() & 15); }

    public Rock getRock3(int x, int z) { return getRockLayer3(x, z); }

    public boolean isStable(int x, int z) { return getStabilityLayer(x, z).valueInt == 0; }

    public int getDrainage(int x, int z) { return getDrainageLayer(x, z).valueInt; }

    public Rock getRockHeight(BlockPos pos) { return getRockHeight(pos.getX(), pos.getY(), pos.getZ()); }

    public Rock getRockHeight(int x, int y, int z) { return getRockLayerHeight(x & 15, y, z & 15); }

    public int getSeaLevelOffset(BlockPos pos) { return getSeaLevelOffset(pos.getX() & 15, pos.getY() & 15); }

    public int getSeaLevelOffset(int x, int z) { return seaLevelOffset[z << 4 | x]; }

    public int getFishPopulation() { return fishPopulation; }

    public List<ChunkDataOreSpawned> getOresSpawned() { return oresSpawnedView; }

    public float getRainfall() { return rainfall; }

    public float getBaseTemp() { return baseTemp; }

    public float getAverageTemp() { return avgTemp; }

    public float getFloraDensity() { return floraDensity; }

    public float getFloraDiversity() { return floraDiversity; }

    public List<Tree> getValidTrees()
    {
        //todo: replace with efficient code (preferably cached?)
        return TFCRegistries.TREES.getValuesCollection().stream()
            .filter(t -> t.isValidLocation(avgTemp, rainfall, floraDensity))
            .sorted((s, t) -> (int) (t.getDominance() - s.getDominance()))
            .collect(Collectors.toList());
    }

    @Nullable
    public Tree getSparseGenTree()
    {
        //todo: replace with efficient, (preferably cached?)
        return TFCRegistries.TREES.getValuesCollection().stream()
            .filter(t -> t.isValidLocation(0.5f * avgTemp + 10f, 0.5f * rainfall + 120f, 0.5f))
            .min((s, t) -> (int) (t.getDominance() - s.getDominance()))
            .orElse(null);
    }

    // Directly accessing the DataLayer is discouraged (except for getting the name). It's easy to use the wrong value.
    public Rock getRockLayer1(int x, int z) { return ((ForgeRegistry<Rock>) TFCRegistries.ROCKS).getValue(rockLayer1[z << 4 | x]); }

    public Rock getRockLayer2(int x, int z) { return ((ForgeRegistry<Rock>) TFCRegistries.ROCKS).getValue(rockLayer2[z << 4 | x]); }

    public Rock getRockLayer3(int x, int z) { return ((ForgeRegistry<Rock>) TFCRegistries.ROCKS).getValue(rockLayer3[z << 4 | x]); }

    public DataLayer getStabilityLayer(int x, int z) { return stabilityLayer[z << 4 | x]; }

    public DataLayer getDrainageLayer(int x, int z) { return drainageLayer[z << 4 | x]; }

    public Rock getRockLayerHeight(int x, int y, int z)
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
            if (instance == null || !instance.isInitialized())
            {
                return new NBTBuilder().setBoolean("valid", false).build();
            }
            NBTTagCompound root = new NBTTagCompound();
            root.setBoolean("valid", true);

            root.setTag("rockLayer1", new NBTTagIntArray(instance.rockLayer1));
            root.setTag("rockLayer2", new NBTTagIntArray(instance.rockLayer2));
            root.setTag("rockLayer3", new NBTTagIntArray(instance.rockLayer3));
            root.setTag("seaLevelOffset", new NBTTagIntArray(instance.seaLevelOffset));

            root.setTag("stabilityLayer", write(instance.stabilityLayer));
            root.setTag("drainageLayer", write(instance.drainageLayer));

            root.setInteger("fishPopulation", instance.fishPopulation);

            root.setFloat("rainfall", instance.rainfall);
            root.setFloat("baseTemp", instance.baseTemp);
            root.setFloat("avgTemp", instance.avgTemp);
            root.setFloat("floraDensity", instance.floraDensity);
            root.setFloat("floraDiversity", instance.floraDiversity);

            NBTTagList chunkDataOreSpawnedNBT = new NBTTagList();
            instance.oresSpawned.stream().map(ChunkDataOreSpawned::serialize).forEach(chunkDataOreSpawnedNBT::appendTag);
            root.setTag("oresSpawned", chunkDataOreSpawnedNBT);

            return root;
        }

        @Override
        public void readNBT(Capability<ChunkDataTFC> capability, ChunkDataTFC instance, EnumFacing side, NBTBase nbt)
        {
            NBTTagCompound root = (NBTTagCompound) nbt;
            if (nbt != null && root.getBoolean("valid"))
            {
                System.arraycopy(root.getIntArray("rockLayer1"), 0, instance.rockLayer1, 0, 256);
                System.arraycopy(root.getIntArray("rockLayer2"), 0, instance.rockLayer2, 0, 256);
                System.arraycopy(root.getIntArray("rockLayer3"), 0, instance.rockLayer3, 0, 256);
                System.arraycopy(root.getIntArray("seaLevelOffset"), 0, instance.seaLevelOffset, 0, 256);

                read(instance.stabilityLayer, root.getByteArray("stabilityLayer"));
                read(instance.drainageLayer, root.getByteArray("drainageLayer"));

                instance.fishPopulation = root.getInteger("fishPopulation");

                instance.rainfall = root.getFloat("rainfall");
                instance.baseTemp = root.getFloat("baseTemp");
                instance.avgTemp = root.getFloat("avgTemp");
                instance.floraDensity = root.getFloat("floraDensity");
                instance.floraDiversity = root.getFloat("floraDiversity");

                instance.oresSpawned.clear();
                root.getTagList("oresSpawned", Constants.NBT.TAG_COMPOUND).forEach(x -> instance.oresSpawned.add(new ChunkDataOreSpawned(((NBTTagCompound) x))));

                instance.initialized = true;
            }
        }
    }
}

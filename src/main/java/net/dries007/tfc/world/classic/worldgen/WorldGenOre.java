/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.util.OreSpawnData;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;
import net.dries007.tfc.world.classic.worldgen.vein.VeinTypeCluster;

public class WorldGenOre implements IWorldGenerator
{

    private static final int NUM_ROLLS = 3;
    private static final int CHUNK_RADIUS = 2;
    public static final int VEIN_MAX_RADIUS = 16 * CHUNK_RADIUS;
    public static final int VEIN_MAX_RADIUS_SQUARED = VEIN_MAX_RADIUS * VEIN_MAX_RADIUS;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!(chunkGenerator instanceof ChunkGenTFC)) return;
        final BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
        ChunkDataTFC chunkData = ChunkDataTFC.get(world, chunkBlockPos);
        if (!chunkData.isInitialized()) return;

        // Check dimension is overworld
        if (world.provider.getDimension() != 0) return;

        List<VeinType> veins = getNearbyVeins(chunkX, chunkZ, world.getSeed(), chunkData);
        if (veins.isEmpty()) return;

        // Set constant values here
        int xoff = chunkX * 16 + 8;
        int zoff = chunkZ * 16 + 8;

        for (VeinType vein : veins)
        {
            // Do checks here that are specific to each vein

            for (int x = 0; x < 16; x++)
            {
                for (int z = 0; z < 16; z++)
                {
                    // Do checks here that are specific to the the horizontal position, not the vertical one
                    if (!vein.inRange(new BlockPos(xoff + x, 0, zoff + z))) continue;

                    for (int y = vein.getLowestY(); y <= vein.getHighestY(); y++)
                    {

                        final BlockPos posAt = new BlockPos(xoff + x, y, z + zoff);
                        final IBlockState stateAt = world.getBlockState(posAt);
                        // Do checks specific to the individual block pos that is getting replaced

                        if (random.nextDouble() > vein.getChanceToGenerate(posAt)) continue;
                        if (!(stateAt.getBlock() instanceof BlockRockVariant)) continue;

                        final BlockRockVariant blockAt = (BlockRockVariant) stateAt.getBlock();
                        if (blockAt.type != Rock.Type.RAW || !vein.oreSpawnData.baseRocks.contains(blockAt.rock))
                            continue;

                        if (vein.oreSpawnData.ore == null && vein.oreSpawnData.state != null)
                        {
                            world.setBlockState(posAt, vein.oreSpawnData.state, 2);
                        }
                        else
                        {
                            world.setBlockState(posAt, BlockOreTFC.get(vein.oreSpawnData.ore, blockAt.rock, vein.grade), 2);
                        }
                    }
                }
            }
        }

        // TODO: remove the "blocks spawned" count from ore vein chunk data. Not worth it to include (because it won't be accurate)
        VeinType veinAtChunk = getVeinAtChunk(chunkX, chunkZ, world.getSeed(), chunkData);
        if (veinAtChunk != null)
        {
            chunkData.addSpawnedOre(veinAtChunk.oreSpawnData.ore, veinAtChunk.oreSpawnData.size, veinAtChunk.grade, veinAtChunk.pos, 0);
        }
    }

    // Used to generate chunk
    private List<VeinType> getNearbyVeins(int chunkX, int chunkZ, long worldSeed, ChunkDataTFC chunkData)
    {
        List<VeinType> veins = new ArrayList<>();

        for (int x = -CHUNK_RADIUS; x <= CHUNK_RADIUS; x++)
        {
            for (int z = -CHUNK_RADIUS; z <= CHUNK_RADIUS; z++)
            {
                VeinType vein = getVeinAtChunk(chunkX + x, chunkZ + z, worldSeed, chunkData);
                if (vein != null) veins.add(vein);
            }
        }

        return veins;
    }

    // Gets veins at a single chunk. Deterministic for a specific chunk x/z and world seed
    @Nullable
    private VeinType getVeinAtChunk(int chunkX, int chunkZ, Long worldSeed, ChunkDataTFC chunkData)
    {
        Random rand = new Random(worldSeed + chunkX * 341873128712L + chunkZ * 132897987541L);

        if (rand.nextDouble() < OreSpawnData.getTotalWeight())
        {
            OreSpawnData.OreEntry oreType;
            BlockPos startPos;
            int rolls = 0;
            while (true)
            {
                oreType = getWeightedOreType(rand);
                startPos = new BlockPos(
                    chunkX * 16 + rand.nextInt(16),
                    oreType.minY + rand.nextInt(oreType.maxY - oreType.minY), // Todo: make max value be the min of top block and maxY ? Is that possible?
                    chunkZ * 16 + rand.nextInt(16)
                );

                if (oreType.baseRocks.contains(chunkData.getRockHeight(startPos.getX(), startPos.getY(), startPos.getZ()).rock))
                {
                    break;
                }

                rolls++;
                if (rolls >= NUM_ROLLS) return null;
            }
            Ore.Grade grade = Ore.Grade.NORMAL;
            if (oreType.ore.graded)
            {
                int gradeInt = rand.nextInt(100);
                if (gradeInt < 20) grade = Ore.Grade.RICH;
                else if (gradeInt < 50) grade = Ore.Grade.POOR;
            }
            return new VeinTypeCluster(startPos, oreType, grade, rand);
        }
        return null;
    }

    @Nonnull
    private OreSpawnData.OreEntry getWeightedOreType(Random rand)
    {
        double r = rand.nextDouble() * OreSpawnData.getTotalWeight();
        double countWeight = 0.0;
        for (OreSpawnData.OreEntry ore : OreSpawnData.getOreSpawnEntries())
        {
            countWeight += ore.weight;
            if (countWeight >= r)
                return ore;
        }
        throw new RuntimeException("Problem choosing random ore weights. Should never be shown");
    }

}
/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.util.collections.WeightedCollection;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.WorldTypeTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;
import net.dries007.tfc.world.classic.worldgen.vein.VeinCluster;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

public class WorldGenOreVeins implements IWorldGenerator
{
    private static final int NUM_ROLLS = 2;
    private static final int CHUNK_RADIUS = 2;
    public static final int VEIN_MAX_RADIUS = 16 * CHUNK_RADIUS;

    // Used to generate chunk
    public static List<Vein> getNearbyVeins(int chunkX, int chunkZ, long worldSeed, int radius)
    {
        List<Vein> veins = new ArrayList<>();

        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                List<Vein> vein = getVeinsAtChunk(chunkX + x, chunkZ + z, worldSeed);
                if (!vein.isEmpty()) veins.addAll(vein);
            }
        }

        return veins;
    }

    // Gets veins at a single chunk. Deterministic for a specific chunk x/z and world seed
    @Nonnull
    private static List<Vein> getVeinsAtChunk(int chunkX, int chunkZ, Long worldSeed)
    {
        Random rand = new Random(worldSeed + chunkX * 341873128712L + chunkZ * 132897987541L);
        List<Vein> veins = new ArrayList<>();

        for (int i = 0; i < NUM_ROLLS; i++)
        {
            WeightedCollection<VeinType> entries = VeinRegistry.INSTANCE.getVeins();
            if (rand.nextDouble() < entries.getTotalWeight())
            {
                VeinType veinType = entries.getRandomEntry(rand);
                BlockPos startPos = new BlockPos(
                    chunkX * 16 + 8 + rand.nextInt(16),
                    veinType.minY + rand.nextInt(veinType.maxY - veinType.minY),
                    chunkZ * 16 + 8 + rand.nextInt(16)
                );

                Ore.Grade grade = Ore.Grade.NORMAL;
                if (veinType.ore != null && veinType.ore.isGraded())
                {
                    int gradeInt = rand.nextInt(100);
                    if (gradeInt < 20) grade = Ore.Grade.RICH;
                    else if (gradeInt < 50) grade = Ore.Grade.POOR;
                }

                veins.add(new VeinCluster(startPos, veinType, grade, rand));
            }
        }
        return veins;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!(chunkGenerator instanceof ChunkGenTFC)) return;
        final BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
        ChunkDataTFC chunkData = ChunkDataTFC.get(world, chunkBlockPos);
        if (!chunkData.isInitialized()) return;

        // Check dimension is overworld
        if (world.provider.getDimension() != 0) return;

        List<Vein> veins = getNearbyVeins(chunkX, chunkZ, world.getSeed(), CHUNK_RADIUS);
        if (veins.isEmpty()) return;

        // Set constant values here
        int xoff = chunkX * 16 + 8;
        int zoff = chunkZ * 16 + 8;

        for (Vein vein : veins)
        {

            // Do checks here that are specific to each vein
            if (!vein.type.baseRocks.contains(chunkData.getRock1(0, 0)) &&
                !vein.type.baseRocks.contains(chunkData.getRock2(0, 0)) &&
                !vein.type.baseRocks.contains(chunkData.getRock3(0, 0)))
                continue;
            if (vein.pos.getY() >= WorldTypeTFC.SEALEVEL + chunkData.getSeaLevelOffset(vein.pos))
                continue;

            boolean generated = false;
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
                        if (blockAt.getType() != Rock.Type.RAW || !vein.type.baseRocks.contains(blockAt.getRock()))
                            continue;

                        world.setBlockState(posAt, vein.type.getOreState(blockAt.getRock(), vein.grade), 2);
                        generated = true;
                    }
                }
            }
            if (generated)
            {
                chunkData.addGeneratedOre(vein.type.ore);
            }
        }
    }
}
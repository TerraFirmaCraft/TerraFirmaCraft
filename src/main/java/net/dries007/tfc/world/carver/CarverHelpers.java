package net.dries007.tfc.world.carver;

import java.util.BitSet;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.WorldCarver;

import net.dries007.tfc.mixin.world.gen.carver.ConfiguredCarverAccessor;
import net.dries007.tfc.world.chunkdata.RockData;

public final class CarverHelpers
{
    public static BitSet createWaterAdjacencyMask(WorldGenRegion world)
    {
        return new BitSet(16 * 16 * (1 + world.getSeaLevel()));
    }

    public static void updateWaterAdjacencyMask(WorldGenRegion world, ChunkPos chunkPos, BitSet waterAdjacencyMask)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final BlockPos originPos = chunkPos.getWorldPosition();
        final int seaLevel = world.getSeaLevel();

        for (int x = -2; x < 18; x++)
        {
            for (int z = -2; z < 18; z++)
            {
                for (int y = 0; y <= seaLevel; y++)
                {
                    mutablePos.setWithOffset(originPos, x, y, z);

                    if (world.getBlockState(mutablePos).getBlock() == Blocks.WATER)
                    {
                        for (int xi = -2; xi <= 2; xi++)
                        {
                            for (int yi = -2; yi <= 0; yi++)
                            {
                                for (int zi = -2; zi <= 2; zi++)
                                {
                                    final int posX = x + xi;
                                    final int posY = y + yi;
                                    final int posZ = z + zi;
                                    if (posX >> 4 == 0 && posZ >> 4 == 0 && y >= 0 && y <= seaLevel)
                                    {
                                        waterAdjacencyMask.set(posX | (posZ << 4) | (posY << 8));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Computes an index into a carving mask bit set, used during world gen
     */
    public static int maskIndex(BlockPos pos)
    {
        return (pos.getX() & 15) | ((pos.getZ() & 15) << 4) | (pos.getY() << 8);
    }

    public static int maskIndex(int x, int y, int z)
    {
        return (x & 15) | ((z & 15) << 4) | (y << 8);
    }

    public static void runCarversWithContext(WorldGenRegion worldIn, IChunk chunk, BiomeManager delegateBiomeManager, BiomeGenerationSettings biomeGenerationSettings, SharedSeedRandom random, GenerationStage.Carving stage, BitSet airCarvingMask, BitSet liquidCarvingMask, RockData rockData, BitSet waterAdjacencyMask)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final List<Supplier<ConfiguredCarver<?>>> carvers = biomeGenerationSettings.getCarvers(stage);

        // Setup IContextCarvers
        for (Supplier<ConfiguredCarver<?>> lazyCarver : carvers)
        {
            final WorldCarver<?> carver = ((ConfiguredCarverAccessor) lazyCarver.get()).accessor$getWorldCarver();
            if (carver instanceof IContextCarver)
            {
                ((IContextCarver) carver).setContext(worldIn, airCarvingMask, liquidCarvingMask, rockData, waterAdjacencyMask);
            }
        }

        // Vanilla carving
        for (int x = chunkPos.x - 8; x <= chunkPos.x + 8; ++x)
        {
            for (int z = chunkPos.z - 8; z <= chunkPos.z + 8; ++z)
            {
                int index = 0;
                for (Supplier<ConfiguredCarver<?>> lazyCarver : carvers)
                {
                    final ConfiguredCarver<?> carver = lazyCarver.get();

                    random.setLargeFeatureSeed(worldIn.getSeed() + index, x, z);
                    if (carver.isStartChunk(random, x, z))
                    {
                        carver.carve(chunk, delegateBiomeManager::getBiome, random, worldIn.getSeaLevel(), x, z, chunkPos.x, chunkPos.z, stage == GenerationStage.Carving.AIR ? airCarvingMask : liquidCarvingMask);
                    }
                    index++;
                }
            }
        }
    }
}

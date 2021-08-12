/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;

public abstract class VeinFeature<C extends VeinConfig, V extends Vein> extends Feature<C>
{
    public VeinFeature(Codec<C> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<C> context)
    {
        final WorldGenLevel worldIn = context.level();
        final BlockPos pos = context.origin();
        final Random random = context.random();
        final C config = context.config();

        final ChunkPos chunkPos = new ChunkPos(pos);
        final List<V> veins = getNearbyVeins(worldIn, chunkPos, config.getChunkRadius(), config, worldIn::getBiome);
        if (!veins.isEmpty())
        {
            for (V vein : veins)
            {
                place(worldIn, random, chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(), vein, config);
            }
            return true;
        }
        return false;
    }

    public final List<V> getNearbyVeins(WorldGenLevel world, ChunkPos pos, int radius, C config, Function<BlockPos, Biome> biomeQuery)
    {
        final List<V> veins = new ArrayList<>();
        final Random random = new Random();
        for (int x = pos.x - radius; x <= pos.x + radius; x++)
        {
            for (int z = pos.z - radius; z <= pos.z + radius; z++)
            {
                getVeinsAtChunk(world, x, z, veins, config, random, biomeQuery);
            }
        }
        return veins;
    }

    public final void getVeinsAtChunk(WorldGenLevel world, int chunkPosX, int chunkPosZ, List<V> veins, C config, Random random, Function<BlockPos, Biome> biomeQuery)
    {
        long seed = LinearCongruentialGenerator.next(world.getSeed(), config.getSalt());
        seed = LinearCongruentialGenerator.next(seed, chunkPosX);
        seed = LinearCongruentialGenerator.next(seed, chunkPosZ);
        seed = LinearCongruentialGenerator.next(seed, config.getSalt());
        random.setSeed(seed);
        if (random.nextInt(config.getRarity()) == 0)
        {
            final V vein = createVein(chunkPosX << 4, chunkPosZ << 4, random, config);
            if (config.canSpawnInBiome(() -> biomeQuery.apply(vein.getPos())))
            {
                veins.add(vein);
            }
        }
    }

    protected void place(WorldGenLevel world, Random random, int blockX, int blockZ, V vein, C config)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final BoundingBox box = getBoundingBox(config, vein);
        box.move(vein.getPos());

        // Intersect the bounding box with the chunk allowed region
        int minX = Math.max(blockX, box.minX()), maxX = Math.min(blockX + 15, box.maxX());
        int minY = Math.max(config.getMinY(), box.minY()), maxY = Math.min(config.getMaxY(), box.maxY());
        int minZ = Math.max(blockZ, box.minZ()), maxZ = Math.min(blockZ + 15, box.maxZ());

        for (int x = minX; x <= maxX; x++)
        {
            for (int z = minZ; z <= maxZ; z++)
            {
                int maxVeinY = -1; // -1 means no veins placed

                for (int y = minY; y <= maxY; y++)
                {
                    mutablePos.set(x, y, z);
                    if (random.nextFloat() < getChanceToGenerate(x - vein.getPos().getX(), y - vein.getPos().getY(), z - vein.getPos().getZ(), vein, config))
                    {
                        final BlockState stoneState = world.getBlockState(mutablePos);
                        final BlockState oreState = getStateToGenerate(stoneState, random, config);
                        if (oreState != null)
                        {
                            world.setBlock(mutablePos, oreState, 3);
                            maxVeinY = y;
                        }
                    }
                }

                final Indicator indicator = config.getIndicator();
                if (indicator != null && maxVeinY != -1 && random.nextInt(indicator.rarity()) == 0)
                {
                    // Pick a random position
                    final int indicatorX = x + random.nextInt(indicator.spread()) - random.nextInt(indicator.spread());
                    final int indicatorZ = z + random.nextInt(indicator.spread()) - random.nextInt(indicator.spread());
                    final int indicatorY = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, indicatorX, indicatorZ);
                    if (Math.abs(indicatorY - maxVeinY) < indicator.depth())
                    {
                        mutablePos.set(indicatorX, indicatorY, indicatorZ);
                        final BlockState stateAt = world.getBlockState(mutablePos);
                        final BlockState state = indicator.getStateToGenerate(random);
                        if (stateAt.isAir() && state.canSurvive(world, mutablePos))
                        {
                            world.setBlock(mutablePos, state, 3);
                            //world.setBlock(mutablePos.above(20), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    @Nullable
    protected BlockState getStateToGenerate(BlockState stoneState, Random random, C config)
    {
        return config.getStateToGenerate(stoneState, random);
    }

    protected final BlockPos defaultPos(int chunkX, int chunkZ, Random random, C config)
    {
        return new BlockPos(chunkX + random.nextInt(16), defaultYPos(config.getSize(), random, config), chunkZ + random.nextInt(16));
    }

    protected final int defaultYPos(int verticalShrinkRange, Random rand, C config)
    {
        final int actualRange = config.getMaxY() - config.getMinY() - 2 * verticalShrinkRange;
        if (actualRange > 0)
        {
            return config.getMinY() + verticalShrinkRange + rand.nextInt(actualRange);
        }
        else
        {
            return (config.getMinY() + config.getMaxY()) / 2;
        }
    }

    /**
     * Gets the chance to generate an ore, using relative position to the center of the vein
     */
    protected abstract float getChanceToGenerate(int x, int y, int z, V vein, C config);

    /**
     * Creates a vein at a given location.
     */
    protected abstract V createVein(int chunkX, int chunkZ, Random random, C config);

    /**
     * Gets the total bounding box around where the vein can spawn, using relative position to the center of the vein
     */
    protected abstract BoundingBox getBoundingBox(C config, V vein);
}

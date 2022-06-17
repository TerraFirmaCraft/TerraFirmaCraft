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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.EnvironmentHelpers;
import org.jetbrains.annotations.Nullable;

public abstract class VeinFeature<C extends VeinConfig, V extends Vein> extends Feature<C>
{
    public VeinFeature(Codec<C> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<C> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final Random random = context.random();
        final C config = context.config();
        final WorldGenerationContext generationContext = new WorldGenerationContext(context.chunkGenerator(), level);

        final ChunkPos chunkPos = new ChunkPos(pos);
        final List<V> veins = getNearbyVeins(level, generationContext, chunkPos, config.getChunkRadius(), config, level::getBiome);
        if (!veins.isEmpty())
        {
            for (V vein : veins)
            {
                place(level, context.chunkGenerator(), random, chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(), vein, config);
            }
            return true;
        }
        return false;
    }

    public final List<V> getNearbyVeins(WorldGenLevel level, WorldGenerationContext context, ChunkPos pos, int radius, C config, Function<BlockPos, Holder<Biome>> biomeQuery)
    {
        final List<V> veins = new ArrayList<>();
        final Random random = new Random();
        for (int x = pos.x - radius; x <= pos.x + radius; x++)
        {
            for (int z = pos.z - radius; z <= pos.z + radius; z++)
            {
                getVeinsAtChunk(level, context, x, z, veins, config, biomeQuery);
            }
        }
        return veins;
    }

    public final void getVeinsAtChunk(WorldGenLevel level, WorldGenerationContext context, int chunkPosX, int chunkPosZ, List<V> veins, C config, Function<BlockPos, Holder<Biome>> biomeQuery)
    {
        final RandomSource forkedRandom = config.random(level.getSeed(), chunkPosX, chunkPosZ);
        if (config.random(level.getSeed(), chunkPosX, chunkPosZ).nextInt(config.getRarity()) == 0)
        {
            final V vein = createVein(context, chunkPosX << 4, chunkPosZ << 4, forkedRandom, config);
            if (config.canSpawnInBiome(biomeQuery.apply(vein.getPos())))
            {
                veins.add(vein);
            }
        }
    }

    protected void place(WorldGenLevel level, ChunkGenerator generator, Random random, int blockX, int blockZ, V vein, C config)
    {
        final boolean debugIndicatorLocations = false;

        final WorldGenerationContext context = new WorldGenerationContext(generator, level);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final BlockPos pos = vein.getPos();
        final BoundingBox box = getBoundingBox(config, vein).moved(pos.getX(), pos.getY(), pos.getZ());

        // Intersect the bounding box with the chunk allowed region
        int minX = Math.max(blockX, box.minX()), maxX = Math.min(blockX + 15, box.maxX());
        int minY = Math.max(config.getMinY(context), box.minY()), maxY = Math.min(config.getMaxY(context), box.maxY());
        int minZ = Math.max(blockZ, box.minZ()), maxZ = Math.min(blockZ + 15, box.maxZ());

        for (int x = minX; x <= maxX; x++)
        {
            for (int z = minZ; z <= maxZ; z++)
            {
                int maxVeinY = -1; // -1 means no veins placed

                for (int y = minY; y <= maxY; y++)
                {
                    mutablePos.set(x, y, z);
                    if (random.nextFloat() < getChanceToGenerate(x - pos.getX(), y - pos.getY(), z - pos.getZ(), vein, config))
                    {
                        final BlockState stoneState = level.getBlockState(mutablePos);
                        final BlockState oreState = getStateToGenerate(stoneState, random, config);
                        if (oreState != null)
                        {
                            level.setBlock(mutablePos, oreState, 3);
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
                    final int indicatorY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, indicatorX, indicatorZ);
                    if (Math.abs(indicatorY - maxVeinY) < indicator.depth())
                    {
                        mutablePos.set(indicatorX, indicatorY, indicatorZ);
                        final BlockState stateAt = level.getBlockState(mutablePos);
                        final BlockState state = FluidHelpers.fillWithFluid(indicator.getStateToGenerate(random), level.getFluidState(mutablePos).getType());
                        if (state != null && EnvironmentHelpers.isWorldgenReplaceable(stateAt) && state.canSurvive(level, mutablePos))
                        {
                            level.setBlock(mutablePos, state, 3);
                            if (debugIndicatorLocations)
                            {
                                level.setBlock(mutablePos.above(20), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
                            }
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

    protected final BlockPos defaultPos(WorldGenerationContext context, int chunkX, int chunkZ, RandomSource random, C config)
    {
        return new BlockPos(chunkX + random.nextInt(16), defaultYPos(context, config.getSize(), random, config), chunkZ + random.nextInt(16));
    }

    protected final int defaultYPos(WorldGenerationContext context, int verticalShrinkRange, RandomSource random, C config)
    {
        final int actualRange = config.getMaxY(context) - config.getMinY(context) - 2 * verticalShrinkRange;
        if (actualRange > 0)
        {
            return config.getMinY(context) + verticalShrinkRange + random.nextInt(actualRange);
        }
        else
        {
            return (config.getMinY(context) + config.getMaxY(context)) / 2;
        }
    }

    /**
     * Gets the chance to generate an ore, using relative position to the center of the vein
     */
    protected abstract float getChanceToGenerate(int x, int y, int z, V vein, C config);

    /**
     * Creates a vein at a given location.
     */
    protected abstract V createVein(WorldGenerationContext context, int chunkX, int chunkZ, RandomSource random, C config);

    /**
     * Gets the total bounding box around where the vein can spawn, using relative position to the center of the vein
     */
    protected abstract BoundingBox getBoundingBox(C config, V vein);
}

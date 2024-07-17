/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;

public abstract class VeinFeature<C extends IVeinConfig, V extends IVein> extends Feature<C>
{
    private static final int MAX_VEIN_Y_NO_ORE_PLACED = Integer.MIN_VALUE;

    public VeinFeature(Codec<C> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<C> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final RandomSource random = context.random();
        final C config = context.config();
        final WorldGenerationContext generationContext = new WorldGenerationContext(context.chunkGenerator(), level);

        final ChunkPos chunkPos = new ChunkPos(pos);
        final List<V> veins = getNearbyVeins(level, generationContext, chunkPos, config.chunkRadius(), config, p -> level.getUncachedNoiseBiome(p.getX(), p.getY(), p.getZ()));
        if (!veins.isEmpty())
        {
            for (V vein : veins)
            {
                place(level, random, chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(), vein, config);
            }
            return true;
        }
        return false;
    }

    public final List<V> getNearbyVeins(WorldGenLevel level, WorldGenerationContext context, ChunkPos pos, int radius, C config, Function<BlockPos, Holder<Biome>> biomeQuery)
    {
        final List<V> veins = new ArrayList<>();
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
        final RandomSource random = new XoroshiroRandomSource(level.getSeed() ^ chunkPosX * 61728364132L, config.config().seed() ^ chunkPosZ * 16298364123L);
        if (random.nextInt(config.config().rarity()) == 0)
        {
            final V vein = createVein(context, chunkPosX << 4, chunkPosZ << 4, random, config);
            if (config.canSpawnAt(vein.pos(), biomeQuery))
            {
                veins.add(vein);
            }
        }
    }

    protected void place(WorldGenLevel level, RandomSource random, int blockX, int blockZ, V vein, C config)
    {
        final boolean debugIndicatorLocations = false;

        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final BlockPos pos = vein.pos();
        final BoundingBox box = getBoundingBox(config, vein).moved(pos.getX(), pos.getY(), pos.getZ());

        final int offsetX, offsetZ;
        if (config.config().projectOffset())
        {
            // Offset needs to be deterministic per vein
            final RandomSource offsetRandom = new XoroshiroRandomSource(Helpers.hash(182739412341L, pos));

            offsetX = offsetRandom.nextInt(16) - offsetRandom.nextInt(16);
            offsetZ = offsetRandom.nextInt(16) - offsetRandom.nextInt(16);
        }
        else
        {
            offsetX = offsetZ = 0;
        }

        // Intersect the bounding box with the chunk allowed region
        final int minX = Math.max(blockX, box.minX()), maxX = Math.min(blockX + 15, box.maxX());
        final int minY = Math.max(config.minY(), box.minY()), maxY = Math.min(config.maxY(), box.maxY());
        final int minZ = Math.max(blockZ, box.minZ()), maxZ = Math.min(blockZ + 15, box.maxZ());

        for (int x = minX; x <= maxX; x++)
        {
            for (int z = minZ; z <= maxZ; z++)
            {
                int maxVeinY = MAX_VEIN_Y_NO_ORE_PLACED;

                final int projectedY = config.config().projectToSurface() ? level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, offsetX + x, offsetZ + z) : 0;

                if (config.config().nearLava() && !isNearLava(level, cursor, x, z))
                {
                    continue;
                }

                for (int y = minY; y <= maxY; y++)
                {
                    if (random.nextFloat() < getChanceToGenerate(x - pos.getX(), y - pos.getY(), z - pos.getZ(), vein, config))
                    {
                        // Now is when we project to surface, after the vein shape has determined that the block is valid
                        cursor.set(x, y + projectedY, z);

                        final BlockState stoneState = level.getBlockState(cursor);
                        final BlockState oreState = getStateToGenerate(stoneState, random, config, x - pos.getX(), y - pos.getY(), z - pos.getZ());
                        if (oreState != null)
                        {
                            level.setBlock(cursor, oreState, 3);
                            maxVeinY = y + projectedY;
                        }
                    }
                }

                final Indicator indicator = config.indicator();
                if (indicator != null && maxVeinY != MAX_VEIN_Y_NO_ORE_PLACED)
                {
                    if (indicator.rarity() > 0 && random.nextInt(indicator.rarity()) == 0) // Above-ground indicators
                    {
                        // Pick a random position within the 3x3 chunk area
                        final int indicatorX = x + random.nextInt(15) - random.nextInt(15);
                        final int indicatorZ = z + random.nextInt(15) - random.nextInt(15);
                        final int indicatorY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, indicatorX, indicatorZ);
                        if (Math.abs(indicatorY - maxVeinY) < indicator.depth())
                        {
                            cursor.set(indicatorX, indicatorY, indicatorZ);

                            final BlockState stateAt = level.getBlockState(cursor);
                            final BlockState state = FluidHelpers.fillWithFluid(indicator.getStateToGenerate(random), stateAt.getFluidState().getType());
                            if (state != null && EnvironmentHelpers.isWorldgenReplaceable(stateAt) && state.canSurvive(level, cursor))
                            {
                                level.setBlock(cursor, state, 3);
                                if (debugIndicatorLocations)
                                {
                                    level.setBlock(cursor.above(20), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
                                }
                            }
                        }
                    }

                    // Below-ground indicators
                    // These are much less likely to find a valid placement in general, so we have a count in addition to a rarity
                    for (int i = 0; i < indicator.undergroundCount(); i++)
                    {
                        if (indicator.undergroundRarity() == 1 || random.nextInt(indicator.undergroundRarity()) == 0)
                        {
                            // Pick a random position within the 3x3x3 chunk area, somewhere within +16/-16 blocks of the vein y-range
                            final int indicatorX = x + random.nextInt(15) - random.nextInt(15);
                            final int indicatorY = minY + (maxY > minY ? random.nextInt(maxY - minY) : 0) + random.nextInt(32) - random.nextInt(8); // Intentionally biased towards above the vein
                            final int indicatorZ = z + random.nextInt(15) - random.nextInt(15);
                            final int maxGroundY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, indicatorX, indicatorZ);

                            if (indicatorY <= maxGroundY - 5)
                            {
                                cursor.set(indicatorX, indicatorY, indicatorZ);

                                final BlockState stateAt = level.getBlockState(cursor);
                                final BlockState state = FluidHelpers.fillWithFluid(indicator.getStateToGenerate(random), stateAt.getFluidState().getType());
                                if (state != null && EnvironmentHelpers.isWorldgenReplaceable(stateAt) && state.canSurvive(level, cursor))
                                {
                                    level.setBlock(cursor, state, 3);
                                    if (debugIndicatorLocations)
                                    {
                                        level.setBlock(cursor.below(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Nullable
    protected BlockState getStateToGenerate(BlockState stoneState, RandomSource random, C config, int x, int y, int z)
    {
        return config.getStateToGenerate(stoneState, random);
    }

    protected final BlockPos defaultPos(int chunkX, int chunkZ, RandomSource random, C config)
    {
        return new BlockPos(chunkX + random.nextInt(16), defaultYPos(config.verticalRadius(), random, config), chunkZ + random.nextInt(16));
    }

    protected final int defaultYPos(int verticalShrinkRange, RandomSource random, C config)
    {
        final int actualRange = config.maxY() - config.minY() - 2 * verticalShrinkRange;
        if (actualRange > 0)
        {
            return config.minY() + verticalShrinkRange + random.nextInt(actualRange);
        }
        else
        {
            return (config.minY() + config.maxY()) / 2;
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

    private boolean isNearLava(WorldGenLevel level, BlockPos.MutableBlockPos cursor, int x, int z)
    {
        final int lavaY = -55;
        for (int lavaX = x - 4; lavaX <= x + 4; lavaX++)
        {
            for (int lavaZ = z - 4; lavaZ <= z + 4; lavaZ++)
            {
                cursor.set(lavaX, lavaY, lavaZ);
                if (level.getFluidState(cursor).getType() == Fluids.LAVA)
                {
                    return true;
                }
            }
        }
        return false;
    }
}

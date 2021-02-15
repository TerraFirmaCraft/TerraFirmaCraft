/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.FastRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Helpers;

public abstract class VeinFeature<C extends VeinConfig, V extends Vein> extends Feature<C>
{
    private final Random chunkRandom;

    public VeinFeature(Codec<C> codec)
    {
        super(codec);

        this.chunkRandom = new Random();
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random random, BlockPos pos, C config)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final List<V> veins = getNearbyVeins(worldIn, chunkPos, config.getChunkRadius(), config);
        if (!veins.isEmpty())
        {
            for (V vein : veins)
            {
                place(worldIn, random, chunkPos.getXStart(), chunkPos.getZStart(), vein, config);
            }
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    protected void place(ISeedReader world, Random random, int blockX, int blockZ, V vein, C config)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final MutableBoundingBox box = getBoundingBox(config);
        box.move(vein.getPos());

        // Intersect the bounding box with the chunk allowed region
        int minX = Math.max(blockX, box.x0), maxX = Math.min(blockX + 15, box.x1);
        int minY = Math.max(config.getMinY(), box.y0), maxY = Math.min(config.getMaxY(), box.y1);
        int minZ = Math.max(blockZ, box.z0), maxZ = Math.min(blockZ + 15, box.z1);

        for (int x = minX; x <= maxX; x++)
        {
            for (int z = minZ; z <= maxZ; z++)
            {
                int maxVeinY = -1; // -1 means no veins placed

                for (int y = minY; y <= maxY; y++)
                {
                    mutablePos.setPos(x, y, z);
                    if (random.nextFloat() < getChanceToGenerate(x - vein.getPos().getX(), y - vein.getPos().getY(), z - vein.getPos().getZ(), vein, config))
                    {
                        final BlockState stoneState = world.getBlockState(mutablePos);
                        final BlockState oreState = getStateToGenerate(stoneState, random, config);
                        if (oreState != null)
                        {
                            world.setBlockState(mutablePos, oreState, 3);
                            maxVeinY = y;
                        }
                    }
                }

                final Indicator indicator = config.getIndicator();
                if (indicator != null && maxVeinY != -1 && random.nextInt(indicator.getRarity()) == 0)
                {
                    // Pick a random position
                    final int indicatorX = x + random.nextInt(indicator.getSpread()) - random.nextInt(indicator.getSpread());
                    final int indicatorZ = z + random.nextInt(indicator.getSpread()) - random.nextInt(indicator.getSpread());
                    final int indicatorY = world.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, indicatorX, indicatorZ);
                    if (Math.abs(indicatorY - maxVeinY) < indicator.getDepth())
                    {
                        mutablePos.setPos(indicatorX, indicatorY, indicatorZ);
                        final BlockState stateAt = world.getBlockState(mutablePos);
                        final BlockState state = indicator.getStateToGenerate(random);
                        if (stateAt.isAir() && state.canBeReplacedByLeaves(world, mutablePos))
                        {
                            world.setBlockState(mutablePos, Helpers.getStateForPlacementWithFluid(world, mutablePos, state).with(HorizontalBlock.HORIZONTAL_FACING, Direction.Plane.HORIZONTAL.random(random)), 3);
                            //world.setBlockState(mutablePos.up(20), Blocks.GOLD_BLOCK.getDefaultState(), 3);
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

    protected final List<V> getNearbyVeins(ISeedReader world, ChunkPos pos, int radius, C config)
    {
        List<V> veins = new ArrayList<>();
        for (int x = pos.x - radius; x <= pos.x + radius; x++)
        {
            for (int z = pos.z - radius; z <= pos.z + radius; z++)
            {
                getVeinsAtChunk(world, x, z, veins, config);
            }
        }
        return veins;
    }

    protected final void getVeinsAtChunk(ISeedReader world, int chunkPosX, int chunkPosZ, List<V> veins, C config)
    {
        long seed = FastRandom.next(world.getSeed(), config.getSalt());
        seed = FastRandom.next(seed, chunkPosX);
        seed = FastRandom.next(seed, chunkPosZ);
        seed = FastRandom.next(seed, config.getSalt());
        chunkRandom.setSeed(seed);
        if (chunkRandom.nextInt(config.getRarity()) == 0)
        {
            veins.add(createVein(chunkPosX << 4, chunkPosZ << 4, chunkRandom, config));
        }
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
    protected abstract MutableBoundingBox getBoundingBox(C config);
}

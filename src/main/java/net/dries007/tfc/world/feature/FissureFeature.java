/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import java.util.*;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.datafixers.Dynamic;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

// todo: need to reduce the frequency with which fissures are able to spawn next to eachother.
public class FissureFeature extends Feature<BlockStateFeatureConfig>
{
    @SuppressWarnings("unused")
    public FissureFeature(Function<Dynamic<?>, ? extends BlockStateFeatureConfig> configFactoryIn)
    {
        super(configFactoryIn);
    }

    public FissureFeature()
    {
        super(BlockStateFeatureConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos startPos, BlockStateFeatureConfig config)
    {
        BlockPos pos = startPos.below(); // start slightly below the surface
        ChunkData data = ChunkDataProvider.get(worldIn).map(provider -> provider.get(pos, ChunkData.Status.ROCKS)).orElseThrow(() -> new IllegalStateException("Missing rock data, cannot generate fissures."));
        Rock bottomRock = data.getRockData().getBottomRock(pos.getX(), pos.getZ());
        BlockState rockState = bottomRock.getBlock(Rock.BlockType.RAW).defaultBlockState();

        int depth = 2 + rand.nextInt(3);
        int radius = 1 + rand.nextInt(2);
        List<BlockPos> clearPositions = getCircle(pos, radius + 2);

        for (int y = 1; y < 4; y++)
        {
            for (BlockPos clear : clearPositions)
            {
                setBlock(worldIn, clear.above(y), Blocks.AIR.defaultBlockState());
            }
        }

        Set<BlockPos> blocks = getCollapseSet(rand, pos, radius, depth);
        for (BlockPos filling : blocks)
        {
            smartFill(worldIn, filling, blocks, rockState, config.state);
        }
        return true;
    }

    /**
     * Gives a list of block positions for a circle.
     * Used to clear the blocks above the fissure
     *
     * @param center the center block
     * @param radius the radius
     * @return ArrayList of blockPos for a circle
     */
    private List<BlockPos> getCircle(BlockPos center, int radius)
    {
        List<BlockPos> list = new ArrayList<>();
        double rSq = Math.pow(radius, 2);
        for (int x = -radius + center.getX(); x <= +radius + center.getX(); x++)
        {
            for (int z = -radius + center.getZ(); z <= +radius + center.getZ(); z++)
            {
                if (Math.pow(x - center.getX(), 2) + Math.pow(z - center.getZ(), 2) <= rSq)
                {
                    list.add(new BlockPos(x, center.getY(), z));
                }
            }
        }
        return list;
    }

    /**
     * Cylinder like fissure.
     *
     * @param random the Random obj from generate to keep it procedural
     * @param center the top-center of the cylinder
     * @param radius the radius of the circle
     * @param depth  the depth of this fissure
     * @return a Set containing block pos to fill with hot water/lava
     */
    private Set<BlockPos> getCollapseSet(Random random, BlockPos center, int radius, int depth)
    {
        int maxOffset = 2 + random.nextInt(radius);
        Set<BlockPos> blocks = new HashSet<>();
        for (int y = 0; y < depth; y++)
        {
            BlockPos centerHeight = center.below(y);
            double rSq = Math.pow(radius, 2);
            for (int x = -radius + centerHeight.getX(); x <= +radius + centerHeight.getX(); x++)
            {
                for (int z = -radius + centerHeight.getZ(); z <= +radius + centerHeight.getZ(); z++)
                {
                    if (Math.pow(x - centerHeight.getX(), 2) + Math.pow(z - centerHeight.getZ(), 2) <= rSq)
                    {
                        BlockPos b = new BlockPos(x, centerHeight.getY(), z);
                        if (random.nextFloat() < 0.65f)
                        {
                            blocks.add(b);
                            for (Direction facing : Direction.values())
                            {
                                if (facing != Direction.UP)
                                {
                                    int off = 0;
                                    while (off < maxOffset && random.nextFloat() < 0.35f)
                                    {
                                        off++;
                                        blocks.add(b.relative(facing));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Now, let's make a "tunnel" all way down so this is gonna look a bit more like a fissure
        int tunnelDepth = depth + 20 + random.nextInt(60);
        int tunnelY = center.below(tunnelDepth).getY();
        if (tunnelY < 20) tunnelY = 20;
        BlockPos tunnelPos = center.below(depth);
        blocks.add(tunnelPos);
        radius = 8;
        while (tunnelPos.getY() > tunnelY)
        {
            int value = random.nextInt(8); // 50% down, 12.5% each side
            if (value < 1)
            {
                tunnelPos = tunnelPos.relative(Direction.NORTH);
            }
            else if (value < 2)
            {
                tunnelPos = tunnelPos.relative(Direction.SOUTH);
            }
            else if (value < 3)
            {
                tunnelPos = tunnelPos.relative(Direction.EAST);
            }
            else if (value < 4)
            {
                tunnelPos = tunnelPos.relative(Direction.WEST);
            }
            else
            {
                tunnelPos = tunnelPos.below();
            }
            // Keep it under control
            if (tunnelPos.getX() > center.getX() + radius)
            {
                tunnelPos = tunnelPos.offset(-1, 0, 0);
            }
            if (tunnelPos.getX() < center.getX() - radius)
            {
                tunnelPos = tunnelPos.offset(1, 0, 0);
            }
            if (tunnelPos.getZ() > center.getZ() + radius)
            {
                tunnelPos = tunnelPos.offset(0, 0, -1);
            }
            if (tunnelPos.getZ() < center.getZ() - radius)
            {
                tunnelPos = tunnelPos.offset(0, 0, 1);
            }
            blocks.add(tunnelPos);
            for (Direction horiz : Direction.Plane.HORIZONTAL)
            {
                blocks.add(tunnelPos.relative(horiz));
            }
        }
        return blocks;
    }

    // A bit smarter fill, try to not fill the "insides" with rock
    // Needs more tweaking
    private void smartFill(IWorld worldIn, BlockPos pos, Set<BlockPos> fillBlockPos, BlockState rock, BlockState fillBlock)
    {
        setBlock(worldIn, pos, fillBlock);
        for (Direction facing : Direction.values())
        {
            if (facing == Direction.UP) continue;
            if (worldIn.getBlockState(pos.relative(facing)) == fillBlock) continue;
            BlockPos rockPos = pos.relative(facing);
            int filledBlocks = 0;
            for (Direction facing2 : Direction.values())
            {
                BlockPos facingPos = rockPos.relative(facing2);
                if (fillBlockPos.contains(facingPos))
                {
                    filledBlocks++;
                }
            }
            if (filledBlocks < 3)
            {
                setBlock(worldIn, rockPos, rock);
            }
            else
            {
                setBlock(worldIn, rockPos, fillBlock);
            }
        }
    }
}
/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.*;
import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableInt;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.soil.DirtBlock;
import net.dries007.tfc.common.blocks.soil.IGrassBlock;
import net.dries007.tfc.common.entities.TFCFallingBlockEntity;
import net.dries007.tfc.common.recipes.BlockRecipeWrapper;
import net.dries007.tfc.common.recipes.LandslideRecipe;

public class ErosionFeature extends Feature<NoFeatureConfig>
{
    public ErosionFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean generate(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        ChunkPos chunkPos = new ChunkPos(pos);
        int chunkX = chunkPos.getXStart(), chunkZ = chunkPos.getZStart();
        int minX = chunkX - 8, maxX = chunkX + 24, minZ = chunkZ - 8, maxZ = chunkZ + 24;

        Map<BlockPos, LandslideRecipe> landslidePositions = new HashMap<>();

        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final BlockRecipeWrapper.Mutable mutableWrapper = new BlockRecipeWrapper.Mutable();

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                final int baseHeight = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, chunkX + x, chunkZ + z);

                // This is a heuristic to avoid both leaving a lot of blocks un-erroded, and also to check erosion down to y=0. After three non-air blocks that we can't errode, we assume we're in the weeds and will not find anything further down.
                final MutableInt tries = new MutableInt();
                for (int y = baseHeight; y >= 0; y--)
                {
                    mutablePos.setPos(chunkX + x, y, chunkZ + z);
                    BlockState stateAt = worldIn.getBlockState(mutablePos);
                    if (stateAt.isAir())
                    {
                        continue;
                    }
                    mutableWrapper.update(chunkX + x, y, chunkZ + z, stateAt);

                    final LandslideRecipe recipe = LandslideRecipe.getRecipe(worldIn.getWorld(), mutableWrapper);
                    if (recipe != null)
                    {
                        landslidePositions.put(mutablePos.toImmutable(), recipe);
                    }
                    else
                    {
                        tries.increment();
                        if (tries.intValue() > 3)
                        {
                            break;
                        }
                    }
                }
            }
        }

        // Ascend upwards, collapsing from the bottom up
        List<BlockPos> sortedLandslidePositions = new ArrayList<>(landslidePositions.keySet());
        sortedLandslidePositions.sort(Comparator.comparingInt(BlockPos::getY));

        for (BlockPos landslidePos : sortedLandslidePositions)
        {
            LandslideRecipe recipe = landslidePositions.get(landslidePos);
            BlockState stateAt = worldIn.getBlockState(landslidePos);
            mutableWrapper.update(landslidePos.getX(), landslidePos.getY(), landslidePos.getZ(), stateAt);
            if (recipe.matches(mutableWrapper, worldIn.getWorld()))
            {
                BlockPos resultPos = quickLandslideBlock(worldIn, landslidePos, rand, minX, maxX, minZ, maxZ);
                if (resultPos != null && !resultPos.equals(landslidePos))
                {
                    worldIn.setBlockState(landslidePos, Blocks.AIR.getDefaultState(), 2);
                    worldIn.setBlockState(resultPos, recipe.getBlockCraftingResult(mutableWrapper), 2);

                    // Fix exposed and/or covered grass
                    if (stateAt.getBlock() instanceof IGrassBlock)
                    {
                        mutablePos.setPos(landslidePos).move(Direction.DOWN, 1);
                        BlockState pastDirtState = worldIn.getBlockState(mutablePos);
                        if (pastDirtState.getBlock() instanceof DirtBlock)
                        {
                            // Replace exposed dirt with grass
                            DirtBlock dirtBlock = (DirtBlock) pastDirtState.getBlock();
                            worldIn.setBlockState(mutablePos, dirtBlock.getGrass(), 2);
                            worldIn.getPendingBlockTicks().scheduleTick(mutablePos, dirtBlock, 0);
                        }

                        mutablePos.setPos(resultPos).move(Direction.DOWN);
                        BlockState pastGrassState = worldIn.getBlockState(mutablePos);
                        if (pastGrassState.getBlock() instanceof IGrassBlock)
                        {
                            // Replace covered grass with dirt
                            IGrassBlock grassBlock = (IGrassBlock) pastGrassState.getBlock();
                            worldIn.setBlockState(mutablePos, grassBlock.getDirt(), 2);
                        }
                    }
                }
            }
        }
        return true;
    }

    @Nullable
    private BlockPos quickLandslideBlock(IBlockReader world, BlockPos pos, Random random, int minX, int maxX, int minZ, int maxZ)
    {
        int minY = Math.max(pos.getY() - 16, 0);
        while (pos.getX() >= minX && pos.getX() <= maxX && pos.getZ() >= minZ && pos.getZ() <= maxZ && pos.getY() > minY)
        {
            // Cascade downwards
            BlockPos down = pos.down();
            while (TFCFallingBlockEntity.canFallThrough(world, down))
            {
                pos = down;
                down = pos.down();
            }
            // Unable to cascade downwards any more, try sideways
            int supportedDirections = 0;
            List<BlockPos> possibleDirections = new ArrayList<>();
            for (Direction side : Direction.Plane.HORIZONTAL)
            {
                if (LandslideRecipe.isSupportedOnSide(world, pos, side))
                {
                    supportedDirections++;
                    if (supportedDirections >= 2)
                    {
                        // Supported by at least two sides, don't fall
                        return pos;
                    }
                }
                else
                {
                    // In order to fall in a direction, we need both the block immediately next to, and the one below to be open
                    BlockPos posSide = pos.offset(side);
                    if (TFCFallingBlockEntity.canFallThrough(world, posSide) && TFCFallingBlockEntity.canFallThrough(world, posSide.down()))
                    {
                        possibleDirections.add(posSide);
                    }
                }
            }
            if (!possibleDirections.isEmpty())
            {
                // Fall to the side
                pos = possibleDirections.get(random.nextInt(possibleDirections.size()));
            }
            else
            {
                // Unable to fall to the side or down, so stop here
                return pos;
            }
        }
        return null;
    }
}
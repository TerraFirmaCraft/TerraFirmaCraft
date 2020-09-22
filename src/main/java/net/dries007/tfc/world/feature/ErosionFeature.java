/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import java.util.*;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import net.dries007.tfc.common.blocks.soil.TFCDirtBlock;
import net.dries007.tfc.common.blocks.soil.TFCGrassBlock;
import net.dries007.tfc.common.entities.TFCFallingBlockEntity;
import net.dries007.tfc.common.recipes.BlockRecipeWrapper;
import net.dries007.tfc.common.recipes.LandslideRecipe;

public class ErosionFeature extends Feature<NoFeatureConfig>
{
    public ErosionFeature()
    {
        super(NoFeatureConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        ChunkPos chunkPos = new ChunkPos(pos);
        int chunkX = chunkPos.getMinBlockX(), chunkZ = chunkPos.getMinBlockZ();
        int minX = chunkX - 8, maxX = chunkX + 24, minZ = chunkZ - 8, maxZ = chunkZ + 24;

        Map<BlockPos, LandslideRecipe> landslidePositions = new HashMap<>();

        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        BlockRecipeWrapper.Mutable mutableWrapper = new BlockRecipeWrapper.Mutable(worldIn.getLevel());

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                for (int y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, chunkX + x, chunkZ + z); y >= 0; y--)
                {
                    mutablePos.set(chunkX + x, y, chunkZ + z);
                    BlockState stateAt = worldIn.getBlockState(mutablePos);
                    if (stateAt.isAir(worldIn, mutablePos))
                    {
                        continue;
                    }
                    mutableWrapper.setPos(chunkX + x, y, chunkZ + z, stateAt);

                    if (LandslideRecipe.getRecipe(worldIn.getLevel(), mutableWrapper).map(recipe -> {
                        landslidePositions.put(mutablePos.immutable(), recipe);
                        return false;
                    }).orElse(true))
                    {
                        break; // No landslide-able surface material
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
            mutableWrapper.setPos(landslidePos.getX(), landslidePos.getY(), landslidePos.getZ(), stateAt);
            if (recipe.matches(mutableWrapper, worldIn.getLevel()))
            {
                BlockPos resultPos = quickLandslideBlock(worldIn, landslidePos, rand, minX, maxX, minZ, maxZ);
                if (resultPos != null && !resultPos.equals(landslidePos))
                {
                    worldIn.setBlock(landslidePos, Blocks.AIR.defaultBlockState(), 2);
                    worldIn.setBlock(resultPos, recipe.getBlockCraftingResult(mutableWrapper), 2);

                    // Fix exposed and/or covered grass
                    if (stateAt.getBlock() instanceof TFCGrassBlock)
                    {
                        mutablePos.set(landslidePos).move(Direction.DOWN, 1);
                        BlockState pastDirtState = worldIn.getBlockState(mutablePos);
                        if (pastDirtState.getBlock() instanceof TFCDirtBlock)
                        {
                            // Replace exposed dirt with grass
                            TFCDirtBlock dirtBlock = (TFCDirtBlock) pastDirtState.getBlock();
                            worldIn.setBlock(mutablePos, dirtBlock.getGrass().defaultBlockState(), 2);
                            worldIn.getBlockTicks().scheduleTick(mutablePos, dirtBlock, 0);
                        }

                        mutablePos.set(resultPos).move(Direction.DOWN);
                        BlockState pastGrassState = worldIn.getBlockState(mutablePos);
                        if (pastGrassState.getBlock() instanceof TFCGrassBlock)
                        {
                            // Replace covered grass with dirt
                            TFCGrassBlock grassBlock = (TFCGrassBlock) pastGrassState.getBlock();
                            worldIn.setBlock(mutablePos, grassBlock.getDirt().defaultBlockState(), 2);
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
            BlockPos down = pos.below();
            while (TFCFallingBlockEntity.canFallThrough(world, down))
            {
                pos = down;
                down = pos.below();
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
                    BlockPos posSide = pos.relative(side);
                    if (TFCFallingBlockEntity.canFallThrough(world, posSide) && TFCFallingBlockEntity.canFallThrough(world, posSide.below()))
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
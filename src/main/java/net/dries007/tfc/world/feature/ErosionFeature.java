/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.entities.TFCFallingBlockEntity;
import net.dries007.tfc.common.recipes.BlockRecipeWrapper;
import net.dries007.tfc.common.recipes.LandslideRecipe;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.RockData;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class ErosionFeature extends Feature<NoneFeatureConfiguration>
{
    public ErosionFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
    {
        final WorldGenLevel worldIn = context.level();
        final BlockPos pos = context.origin();

        final ChunkPos chunkPos = new ChunkPos(pos);
        final int chunkX = chunkPos.getMinBlockX(), chunkZ = chunkPos.getMinBlockZ();
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final BlockRecipeWrapper.Mutable mutableWrapper = new BlockRecipeWrapper.Mutable();
        final RockData rockData = ChunkDataProvider.get(context.chunkGenerator()).get(chunkPos).getRockDataOrThrow();

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                // Top down iteration, attempt to either fix unstable locations, or remove the offending blocks.
                final int baseHeight = worldIn.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkX + x, chunkZ + z);
                boolean prevBlockCanLandslide = false;
                int lastSafeY = baseHeight;

                mutablePos.set(chunkX + x, baseHeight, chunkZ + z);

                // Iterate down to sea level - at that point we're pretty likely not to come upon any significant collapse areas due to the water adjacency mask used in carving
                for (int y = baseHeight; y >= TFCChunkGenerator.SEA_LEVEL; y--)
                {
                    mutablePos.setY(y);
                    BlockState stateAt = worldIn.getBlockState(mutablePos);

                    mutableWrapper.update(chunkX + x, y, chunkZ + z, stateAt);
                    LandslideRecipe recipe = stateAt.isAir() ? null : LandslideRecipe.getRecipe(worldIn.getLevel(), mutableWrapper);
                    if (prevBlockCanLandslide)
                    {
                        // Continuing a collapsible downwards
                        // If the block is also collapsible, we just continue until we reach either the bottom (solid) or something to collapse through
                        if (recipe == null)
                        {
                            // This block is sturdy, preventing the column from collapsing
                            // However, we need to make sure we can't collapse *through* this block
                            if (stateAt.isAir() || TFCFallingBlockEntity.canFallThrough(worldIn, mutablePos, stateAt))
                            {
                                // We can collapse through the current block. aka, from [y + 1, lastSafeY) need to collapse
                                // If we would only collapse one block, we remove it. Otherwise, we replace the lowest block with hardened stone
                                if (lastSafeY > y + 2)
                                {
                                    // More than one block to collapse, so we can support instead
                                    mutablePos.setY(y + 1);
                                    worldIn.setBlock(mutablePos, rockData.getRock(x, y + 1, z).getBlock(Rock.BlockType.RAW).defaultBlockState(), 2);
                                }
                                else
                                {
                                    // Delete the block above
                                    mutablePos.setY(y + 1);
                                    worldIn.removeBlock(mutablePos, false);
                                }
                            }
                            prevBlockCanLandslide = false;
                            lastSafeY = y;
                        }
                    }
                    else
                    {
                        // Last block is sturdy
                        if (recipe == null)
                        {
                            // This block is sturdy
                            lastSafeY = y;
                        }
                        else
                        {
                            // This block can collapse. lastSafeY will already be y + 1, so all we need to mark is the prev flag for next iteration
                            prevBlockCanLandslide = true;
                        }
                    }
                }
            }
        }
        return true;
    }
}
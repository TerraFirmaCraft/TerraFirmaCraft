/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.entities.TFCFallingBlockEntity;
import net.dries007.tfc.common.recipes.LandslideRecipe;
import net.dries007.tfc.world.MutableDensityFunctionContext;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.ChunkGeneratorExtension;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.settings.RockLayerSettings;

public class ErosionFeature extends Feature<NoneFeatureConfiguration>
{
    public ErosionFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();

        final ChunkAccess chunk = level.getChunk(pos);
        final ChunkPos chunkPos = new ChunkPos(pos);
        final int chunkX = chunkPos.getMinBlockX(), chunkZ = chunkPos.getMinBlockZ();
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final RockData rockData = ChunkDataProvider.get(context.chunkGenerator()).get(chunk).getRockData();

        final ChunkGeneratorExtension extension = (ChunkGeneratorExtension) context.chunkGenerator();
        final RockLayerSettings rockSettings = extension.getRockLayerSettings();
        final Aquifer aquifer = extension.getOrCreateAquifer(chunk);
        final MutableDensityFunctionContext point = new MutableDensityFunctionContext(mutablePos);

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                // Top down iteration, attempt to either fix unstable locations, or remove the offending blocks.
                final int baseHeight = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkX + x, chunkZ + z);
                boolean prevBlockCanLandslide = false;
                int lastSafeY = baseHeight;
                Block prevBlockHardened = null;

                mutablePos.set(chunkX + x, baseHeight, chunkZ + z);

                for (int y = baseHeight; y >= context.chunkGenerator().getMinY(); y--)
                {
                    mutablePos.setY(y);

                    BlockState stateAt = level.getBlockState(mutablePos);
                    LandslideRecipe recipe = stateAt.isAir() ? null : LandslideRecipe.getRecipe(stateAt);
                    boolean stateAtIsFragile = stateAt.isAir() || TFCFallingBlockEntity.canFallThrough(level, mutablePos, stateAt);
                    if (prevBlockCanLandslide)
                    {
                        // Continuing a collapsible downwards
                        // If the block is also collapsible, we just continue until we reach either the bottom (solid) or something to collapse through
                        if (recipe == null)
                        {
                            // This block is sturdy, preventing the column from collapsing
                            // However, we need to make sure we can't collapse *through* this block
                            if (stateAtIsFragile)
                            {
                                // We can collapse through the current block. aka, from [y + 1, lastSafeY) need to collapse
                                // If we would only collapse one block, we remove it. Otherwise, we replace the lowest block with hardened stone
                                if (lastSafeY > y + 2)
                                {
                                    // More than one block to collapse, so we can support instead
                                    mutablePos.setY(y + 1);
                                    level.setBlock(mutablePos, rockData.getRock(x, y + 1, z).hardened().defaultBlockState(), 2);
                                }
                                else
                                {
                                    // See if we can delete the block above (if the above of that is air)
                                    // We then choose either a solid or full block by passing in a positive or negative value to the aquifer's computeState
                                    mutablePos.setY(y + 2);
                                    final boolean blockAboveIsAir = level.getBlockState(mutablePos).isAir();

                                    mutablePos.setY(y + 1);
                                    final BlockState airOrLiquidState = aquifer.computeSubstance(point, -1);

                                    if (blockAboveIsAir && airOrLiquidState != null)
                                    {
                                        level.setBlock(mutablePos, airOrLiquidState, 2);
                                    }
                                    else
                                    {
                                        // Otherwise, we have to support the block, and the only way we can is by placing stone.
                                        mutablePos.setY(y + 1);
                                        level.setBlock(mutablePos, rockData.getRock(x, y + 1, z).hardened().defaultBlockState(),2);
                                    }
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

                    // Update stone from raw -> hardened
                    if (stateAtIsFragile)
                    {
                        if (prevBlockHardened != null)
                        {
                            mutablePos.setY(y + 1);
                            level.setBlock(mutablePos, prevBlockHardened.defaultBlockState(), 2);
                        }
                        prevBlockHardened = null;
                    }
                    else
                    {
                        prevBlockHardened = rockSettings.getHardened(stateAt.getBlock());
                    }
                }
            }
        }
        return true;
    }
}
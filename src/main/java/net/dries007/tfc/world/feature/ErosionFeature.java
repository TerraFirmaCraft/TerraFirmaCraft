/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import com.mojang.serialization.Codec;
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

import net.dries007.tfc.common.entities.misc.TFCFallingBlockEntity;
import net.dries007.tfc.common.recipes.LandslideRecipe;
import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.MutableDensityFunctionContext;
import net.dries007.tfc.world.chunkdata.ChunkData;
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
        final RockData rockData = ChunkData.get(chunk).getRockData();

        final ChunkGeneratorExtension extension = (ChunkGeneratorExtension) context.chunkGenerator();
        final RockLayerSettings rockSettings = extension.rockLayerSettings();
        final Aquifer aquifer = extension.getOrCreateAquifer(chunk);
        final MutableDensityFunctionContext point = new MutableDensityFunctionContext(mutablePos);
        final int minY = context.chunkGenerator().getMinY();

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

                for (int y = baseHeight; y >= minY; y--)
                {
                    mutablePos.setY(y);

                    BlockState stateAt = chunk.getBlockState(mutablePos);
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
                                    setBlock(level, chunk, mutablePos, rockData.getRock(chunkX + x, y + 1, chunkZ + z).hardened().defaultBlockState());
                                }
                                else
                                {
                                    // See if we can delete the block above (if the above of that is air)
                                    // We then choose either a solid or full block by passing in a positive or negative value to the aquifer's computeState
                                    mutablePos.setY(y + 2);
                                    final boolean blockAboveIsAir = chunk.getBlockState(mutablePos).isAir();

                                    mutablePos.setY(y + 1);
                                    final BlockState airOrLiquidState = aquifer.computeSubstance(point, -1);

                                    if (blockAboveIsAir && airOrLiquidState != null)
                                    {
                                        setBlock(level, chunk, mutablePos, airOrLiquidState);
                                    }
                                    else
                                    {
                                        // Otherwise, we have to support the block, and the only way we can is by placing stone.
                                        mutablePos.setY(y + 1);
                                        setBlock(level, chunk, mutablePos, rockData.getRock(chunkX + x, y + 1, chunkZ + z).hardened().defaultBlockState());
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
                            setBlock(level, chunk, mutablePos, prevBlockHardened.defaultBlockState());
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

    /**
     * Faster than using {@link net.minecraft.server.level.WorldGenRegion#setBlock(BlockPos, BlockState, int)} or variants. Optimized as we're not setting any block entities or need to re-query the chunk and check in-range.
     * Worthwhile as erosion feature is responsible for a sizazble chunk of all feature generation time.
     */
    private void setBlock(WorldGenLevel level, ChunkAccess chunk, BlockPos pos, BlockState state)
    {
        final BlockState prevState = chunk.setBlockState(pos, state, false);
        if (prevState != null && prevState.hasBlockEntity())
        {
            chunk.removeBlockEntity(pos);
        }
        if (state.hasPostProcess(level, pos))
        {
            chunk.markPosForPostprocessing(pos);
        }
    }
}
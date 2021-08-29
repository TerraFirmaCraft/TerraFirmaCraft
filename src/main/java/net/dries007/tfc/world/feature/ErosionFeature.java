/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.entities.TFCFallingBlockEntity;
import net.dries007.tfc.common.recipes.LandslideRecipe;
import net.dries007.tfc.common.recipes.inventory.BlockRecipeWrapper;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.ChunkGeneratorExtension;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;

public class ErosionFeature extends Feature<NoneFeatureConfiguration>
{
    @SuppressWarnings("ConstantConditions")
    private static final LandslideRecipe CACHE_MISS = new LandslideRecipe(null, null, null, false);

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
        final BlockRecipeWrapper.Mutable wrapper = new BlockRecipeWrapper.Mutable();
        final RockData rockData = ChunkDataProvider.get(context.chunkGenerator()).get(chunkPos).getRockDataOrThrow();
        final RockLayerSettings rockSettings = ((ChunkGeneratorExtension) context.chunkGenerator()).getRockLayerSettings();

        // Avoid repeated recipe queries for blocks
        // This does make some simplifying assumptions about landslide recipes, and the types present in world gen, that we are ignoring here.
        final Map<BlockState, LandslideRecipe> cachedRecipes = new HashMap<>();
        final Map<Block, Block> hardeningBlocks = rockSettings.getRocks().stream().collect(Collectors.toMap(RockSettings::raw, RockSettings::hardened));

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                // Top down iteration, attempt to either fix unstable locations, or remove the offending blocks.
                final int baseHeight = worldIn.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkX + x, chunkZ + z);
                boolean prevBlockCanLandslide = false;
                boolean prevBlockCanCollapse = false;
                int lastSafeY = baseHeight;
                Block prevBlockHardened = null;

                mutablePos.set(chunkX + x, baseHeight, chunkZ + z);

                // Iterate down to sea level
                for (int y = baseHeight; y >= context.chunkGenerator().getMinY(); y--)
                {
                    mutablePos.setY(y);

                    BlockState stateAt = worldIn.getBlockState(mutablePos);
                    LandslideRecipe recipe = cachedRecipes.get(stateAt);
                    if (recipe == CACHE_MISS)
                    {
                        recipe = null;
                    }
                    else if (recipe == null)
                    {
                        wrapper.update(chunkX + x, y, chunkZ + z, stateAt);
                        recipe = LandslideRecipe.getRecipe(worldIn.getLevel(), wrapper);
                        cachedRecipes.put(stateAt, recipe != null ? recipe : CACHE_MISS);
                    }

                    boolean stateAtIsFragile = stateAt.isAir() || TFCFallingBlockEntity.canFallThrough(worldIn, mutablePos, stateAt);
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
                                    worldIn.setBlock(mutablePos, rockData.getRock(x, y + 1, z).hardened().defaultBlockState(), 2);
                                }
                                else
                                {
                                    // Delete the block above
                                    mutablePos.setY(y + 1);
                                    worldIn.removeBlock(mutablePos, false);
                                }
                                continue;
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
                    // if (true) continue;
                    // Update stone from raw -> hardened
                    if (stateAtIsFragile)
                    {
                        if (prevBlockHardened != null)
                        {
                            mutablePos.setY(y + 1);
                            worldIn.setBlock(mutablePos, prevBlockHardened.defaultBlockState(), 2);
                        }
                        prevBlockHardened = null;
                    }
                    else
                    {
                        prevBlockHardened = hardeningBlocks.get(stateAt.getBlock());
                    }
                }
            }
        }
        return true;
    }
}
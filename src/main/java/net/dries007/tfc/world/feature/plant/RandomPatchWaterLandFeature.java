/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public class RandomPatchWaterLandFeature extends Feature<BlockClusterFeatureConfig>
{
    public RandomPatchWaterLandFeature(Codec<BlockClusterFeatureConfig> codec)
    {
        super(codec);
    }

    //unused: project, canReplace, y spread
    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, BlockClusterFeatureConfig config)
    {
        BlockState state = config.stateProvider.getState(rand, pos);
        int i = 0;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int j = 0; j < config.tries; ++j)
        {
            mutablePos.setWithOffset(world.getHeightmapPos(Heightmap.Type.OCEAN_FLOOR_WG, pos), rand.nextInt(config.xspread + 1) - rand.nextInt(config.xspread + 1), -1, rand.nextInt(config.zspread + 1) - rand.nextInt(config.zspread + 1));
            BlockState belowState = world.getBlockState(mutablePos);
            mutablePos.move(Direction.UP);
            boolean flag1 = world.isEmptyBlock(mutablePos);
            boolean flag2 = world.hasWater(mutablePos);
            boolean flag3 = (config.whitelist.isEmpty() || config.whitelist.contains(belowState.getBlock())) && !config.blacklist.contains(belowState);
            if ((world.isEmptyBlock(mutablePos) || world.hasWater(mutablePos)) && state.canBeReplacedByLeaves(world, mutablePos) && (config.whitelist.isEmpty() || config.whitelist.contains(belowState.getBlock())) && !config.blacklist.contains(belowState))
            {
                if (state.getBlock() instanceof IFluidLoggable)
                {
                    IFluidLoggable block = (IFluidLoggable) state.getBlock();
                    state = block.getStateWithFluid(state, world.getFluidState(mutablePos).getType());
                }
                config.blockPlacer.place(world, mutablePos, state, rand);
                ++i;
            }
        }
        return i > 0;
    }
}

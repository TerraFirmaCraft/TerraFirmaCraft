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
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public class RandomPatchWaterFeature extends Feature<BlockClusterFeatureConfig>
{
    public RandomPatchWaterFeature(Codec<BlockClusterFeatureConfig> codec)
    {
        super(codec);
    }

    //unused: project, canReplace
    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, BlockClusterFeatureConfig config)
    {
        BlockState blockstate = config.stateProvider.getState(rand, pos);
        int i = 0;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int j = 0; j < config.tries; ++j)
        {
            mutablePos.setWithOffset(world.getHeightmapPos(Heightmap.Type.OCEAN_FLOOR_WG, pos), rand.nextInt(config.xspread + 1) - rand.nextInt(config.xspread + 1), rand.nextInt(config.yspread + 1) - rand.nextInt(config.yspread + 1) - 1, rand.nextInt(config.zspread + 1) - rand.nextInt(config.zspread + 1));
            BlockState state = world.getBlockState(mutablePos);
            mutablePos.move(Direction.UP);
            if ((world.hasWater(mutablePos)) && !(world.getBlockState(mutablePos).getBlock() instanceof IFluidLoggable) && state.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON) && (config.whitelist.isEmpty() || config.whitelist.contains(state.getBlock())) && !config.blacklist.contains(state))
            {
                config.blockPlacer.place(world, mutablePos, blockstate, rand);
                ++i;
            }
        }
        return i > 0;
    }
}

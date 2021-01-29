/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;

public class CavePatchFeature extends Feature<BlockClusterFeatureConfig>
{
    public CavePatchFeature(Codec<BlockClusterFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, BlockClusterFeatureConfig config)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        boolean placedAny = false;
        for (int i = 0; i < config.tries; ++i)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(config.xspread + 1) - rand.nextInt(config.xspread + 1), -1, rand.nextInt(config.zspread + 1) - rand.nextInt(config.zspread + 1));
            final BlockState belowState = world.getBlockState(mutablePos);
            mutablePos.move(Direction.UP);
            final BlockState state = config.stateProvider.getState(rand, mutablePos);

            if (world.isEmptyBlock(mutablePos) && state.canSurvive(world, mutablePos) && (config.whitelist.isEmpty() || config.whitelist.contains(belowState.getBlock())) && !config.blacklist.contains(belowState))
            {
                config.blockPlacer.place(world, mutablePos, state, rand);
                placedAny = true;
            }
        }
        return placedAny;
    }
}

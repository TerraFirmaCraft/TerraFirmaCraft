/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Helpers;

public class TFCVinesFeature extends Feature<BlockStateConfiguration>
{
    public TFCVinesFeature(Codec<BlockStateConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos blockpos = context.origin();
        BlockState state = context.config().state;
        if (level.isEmptyBlock(blockpos))
        {
            for (Direction direction : Helpers.DIRECTIONS)
            {
                if (direction != Direction.DOWN && VineBlock.isAcceptableNeighbour(level, blockpos.relative(direction), direction))
                {
                    level.setBlock(blockpos, state.setValue(VineBlock.getPropertyForFace(direction), true), 2);
                    return true;
                }
            }
        }
        return false;
    }
}

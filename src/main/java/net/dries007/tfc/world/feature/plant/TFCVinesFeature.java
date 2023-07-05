/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;


import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

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
        final BlockPos pos = context.origin();
        BlockState state = context.config().state;
        if (level.isEmptyBlock(pos))
        {
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                if (VineBlock.isAcceptableNeighbour(level, pos.relative(direction), direction))
                {
                    state = state.setValue(VineBlock.getPropertyForFace(direction), true);
                    level.setBlock(pos, state, 2);
                    placeColumnBelow(level, pos, state, context.random().nextInt(14));
                    return true;
                }
            }
        }
        return false;
    }

    private void placeColumnBelow(WorldGenLevel level, BlockPos pos, BlockState state, int maxLength)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos().set(pos);
        for (int i = 0; i < maxLength; i++)
        {
            cursor.move(0, -1, 0);
            if (level.getBlockState(cursor).isAir())
            {
                level.setBlock(cursor, state, 2);
            }
            else
            {
                return;
            }
        }
    }

}

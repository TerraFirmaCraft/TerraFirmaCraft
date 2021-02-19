/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.block.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.Placement;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;

public class ShorelineDecorator extends Placement<NearWaterConfig>
{
    public ShorelineDecorator(Codec<NearWaterConfig> codec)
    {
        super(codec);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random random, NearWaterConfig config, BlockPos pos)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final int radius = config.getRadius();
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                mutablePos.setPos(pos).move(x, 0, z);
                //get height
                mutablePos.setY(helper.func_242893_a(Heightmap.Type.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ()));
                BlockState state = helper.func_242894_a(mutablePos);//get block state
                if (!state.isAir())
                    return Stream.empty();
                mutablePos.move(Direction.DOWN);
                state = helper.func_242894_a(mutablePos);
                if (state.isIn(TFCTags.Blocks.BUSH_PLANTABLE_ON) || state.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
                {
                    for (Direction d : Direction.Plane.HORIZONTAL)
                    {
                        mutablePos.move(d);
                        if (helper.func_242894_a(mutablePos).getFluidState().isTagged(FluidTags.WATER))
                        {
                            return Stream.of(pos);
                        }
                        mutablePos.move(d.getOpposite());
                    }
                }
            }
        }
        return Stream.empty();
    }
}

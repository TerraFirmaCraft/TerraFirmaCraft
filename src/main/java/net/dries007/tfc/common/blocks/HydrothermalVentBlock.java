/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.util.Helpers;

public class HydrothermalVentBlock extends ExtendedBlock
{
    public HydrothermalVentBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos belowPos = pos.below();
        return level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return facing == Direction.DOWN && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        final BlockPos above = pos.above();
        if (level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN))
            return;
        final double x = pos.getX() + 0.5;
        final double y = pos.getY() + 1.0;
        final double z = pos.getZ() + 0.5;
        for (int i = 0; i < 1 + random.nextInt(3); i++)
        {
            level.addAlwaysVisibleParticle(TFCParticles.SMOKES.get(2).get(), x + Helpers.triangle(random) * 0.3f, y, z + Helpers.triangle(random) * 0.3f, 0, 0.07D, 0);
        }
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.world.river.Flow;

public class RiverWaterFluid extends WaterFluid implements FlowingFluidExtension
{
    public static final EnumProperty<Flow> FLOW = TFCBlockStateProperties.FLOW;

    private static boolean isSameAbove(FluidState fluid, BlockGetter level, BlockPos pos)
    {
        return fluid.getType().isSame(level.getFluidState(pos.above()).getType());
    }

    @Override
    public boolean isSource(FluidState state)
    {
        return true;
    }

    @Override
    public boolean isSame(Fluid fluid)
    {
        return super.isSame(fluid) || fluid == TFCFluids.RIVER_WATER.get();
    }

    @Override
    public FluidType getFluidType()
    {
        return NeoForgeMod.WATER_TYPE.value();
    }

    /**
     * Override to check {@link #isSame(Fluid)} for the above block, including river water.
     */
    @Override
    public float getHeight(FluidState fluid, BlockGetter level, BlockPos pos)
    {
        return isSameAbove(fluid, level, pos) ? 1f : getOwnHeight(fluid);
    }

    @Override
    public BlockState createLegacyBlock(FluidState state)
    {
        return TFCBlocks.RIVER_WATER.get().defaultBlockState().setValue(FLOW, state.getValue(FLOW));
    }

    @Override
    public FluidState getSource(LevelReader level, BlockPos pos, boolean falling)
    {
        // Average contribution from four corners
        final Flow flow = Flow.lerp(
            getFlowFromDirection(level, pos, Direction.NORTH),
            getFlowFromDirection(level, pos, Direction.EAST),
            getFlowFromDirection(level, pos, Direction.WEST),
            getFlowFromDirection(level, pos, Direction.SOUTH),
            0.5f, 0.5f
        );
        if (flow == Flow.NONE)
        {
            return getSource(falling);
        }
        return TFCFluids.RIVER_WATER.get().defaultFluidState().setValue(BlockStateProperties.FALLING, falling).setValue(FLOW, flow);
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder)
    {
        super.createFluidStateDefinition(builder.add(FLOW));
    }

    /**
     * Override to return a flow value based on the state.
     */
    @Override
    public Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState fluid)
    {
        final Flow flow = fluid.getValue(FLOW);

        Vec3 vector = flow.getVector();
        if (fluid.getValue(FALLING))
        {
            final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                cursor.setWithOffset(pos, direction);
                if (isSolidFace(level, cursor, direction) || isSolidFace(level, cursor.above(), direction))
                {
                    vector = vector.normalize().add(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }
        return vector.normalize();
    }

    @Override
    public int getAmount(FluidState state)
    {
        return 8;
    }

    private Flow getFlowFromDirection(LevelReader level, BlockPos pos, Direction direction)
    {
        final FluidState adjacentFluid = level.getFluidState(pos.relative(direction));
        if (adjacentFluid.hasProperty(TFCBlockStateProperties.FLOW))
        {
            return adjacentFluid.getValue(TFCBlockStateProperties.FLOW);
        }
        return Flow.NONE;
    }
}

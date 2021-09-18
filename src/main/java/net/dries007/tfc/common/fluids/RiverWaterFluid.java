package net.dries007.tfc.common.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.world.river.Flow;

public class RiverWaterFluid extends WaterFluid
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

        Vec3 vector = new Vec3(flow.getX(), 0.0D, flow.getZ());
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
}

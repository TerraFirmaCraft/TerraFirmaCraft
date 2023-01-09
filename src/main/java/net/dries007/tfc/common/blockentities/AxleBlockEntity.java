package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.mechanical.AxleBlock;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.capabilities.power.IRotationProvider;
import net.dries007.tfc.common.capabilities.power.RotationCapability;

public class AxleBlockEntity extends TFCBlockEntity
{

    private final SidedHandler.Builder<IRotationProvider> handler;

    public AxleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        handler = new SidedHandler.Builder<IRotationProvider>()
            .on(new AxleRotationHandler(this, Direction.NORTH), Direction.NORTH)
            .on(new AxleRotationHandler(this, Direction.SOUTH), Direction.SOUTH)
            .on(new AxleRotationHandler(this, Direction.WEST), Direction.WEST)
            .on(new AxleRotationHandler(this, Direction.EAST), Direction.EAST);
    }

    public AxleBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.AXLE.get(), pos, state);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == RotationCapability.ROTATION)
        {
            return handler.getSidedHandler(side).cast();
        }
        return super.getCapability(cap, side);
    }

    public static class AxleRotationHandler implements IRotationProvider
    {
        private final AxleBlockEntity axle;
        private final Direction side;

        public AxleRotationHandler(AxleBlockEntity axle, Direction side)
        {
            this.axle = axle;
            this.side = side;
        }

        public boolean isCorrectDirection()
        {
            return side.getAxis() == getState().getValue(AxleBlock.AXIS);
        }

        @Override
        public boolean isPowered()
        {
            return isCorrectDirection() && getState().getValue(AxleBlock.ROTATING);
        }

        @Override
        public boolean setPowered(boolean powered)
        {
            assert axle.level != null;
            if (isCorrectDirection())
            {
                final BlockState state = getState();
                if (state.getValue(AxleBlock.ROTATING) != powered)
                {
                    axle.level.setBlockAndUpdate(axle.getBlockPos(), state.setValue(AxleBlock.ROTATING, powered));
                }
                return true;
            }
            return false;
        }

        /**
         * By experience, we cannot rely on getBlockState() to be accurate / exist.
         */
        public BlockState getState()
        {
            assert axle.level != null;
            return axle.level.getBlockState(axle.getBlockPos());
        }
    }
}

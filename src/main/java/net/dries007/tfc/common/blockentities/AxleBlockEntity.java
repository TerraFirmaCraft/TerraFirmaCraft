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
import net.dries007.tfc.util.Helpers;

public class AxleBlockEntity extends TFCBlockEntity implements IRotationProvider
{
    private final SidedHandler.Builder<IRotationProvider> handler;

    public AxleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        handler = new SidedHandler.Builder<>(this);
    }

    public AxleBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.AXLE.get(), pos, state);
    }

    @Override
    public boolean isPowered()
    {
        assert level != null;
        final BlockState state = level.getBlockState(getBlockPos());
        return state.getBlock() instanceof AxleBlock && state.getValue(AxleBlock.ROTATING);
    }

    @Override
    public void setPowered(boolean powered)
    {
        assert level != null;
        final BlockState state = level.getBlockState(getBlockPos());
        level.setBlockAndUpdate(getBlockPos(), Helpers.setProperty(state, AxleBlock.ROTATING, powered));
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == RotationCapability.ROTATION && side != null)
        {
            return handler.getSidedHandler(side).cast();
        }
        return super.getCapability(cap, side);
    }
}

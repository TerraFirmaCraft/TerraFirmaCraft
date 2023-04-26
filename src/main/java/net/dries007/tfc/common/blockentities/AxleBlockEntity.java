/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.mechanical.AxleBlock;
import net.dries007.tfc.common.capabilities.power.IRotator;
import net.dries007.tfc.common.capabilities.power.RotationCapability;

public class AxleBlockEntity extends RotatingBlockEntity
{
    private final LazyOptional<IRotator> handler;

    public AxleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        handler = LazyOptional.of(() -> this);
    }

    public AxleBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.AXLE.get(), pos, state);
    }

    @Override
    public void setSignal(int signal)
    {
        super.setSignal(signal);
        boolean powered = signal > 0;
        if (powered != getBlockState().getValue(AxleBlock.POWERED) && level != null)
        {
            level.setBlockAndUpdate(worldPosition, getBlockState().cycle(AxleBlock.POWERED));
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == RotationCapability.ROTATION && (side == null || isCorrectDirection(side)))
        {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    public boolean isCorrectDirection(Direction side)
    {
        return side.getAxis() == getBlockState().getValue(AxleBlock.AXIS);
    }

    @Override
    public boolean hasShaft(LevelAccessor level, BlockPos pos, Direction facing)
    {
        return isCorrectDirection(facing);
    }

}

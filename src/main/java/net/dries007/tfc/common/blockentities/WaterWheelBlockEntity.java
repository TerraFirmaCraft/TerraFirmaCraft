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

import net.dries007.tfc.common.blocks.mechanical.WaterWheelBlock;
import net.dries007.tfc.common.capabilities.power.IRotator;
import net.dries007.tfc.common.capabilities.power.RotationCapability;

public class WaterWheelBlockEntity extends RotatingBlockEntity
{
    private final LazyOptional<IRotator> handler = LazyOptional.of(() -> this);

    public WaterWheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public int getSignal()
    {
        return super.getSignal();
    }

    @Override
    public void setSignal(int signal) { }

    @Override
    public boolean isSource()
    {
        return true;
    }

    @Override
    public boolean hasShaft(LevelAccessor level, BlockPos pos, Direction facing)
    {
        return isCorrectDirection(facing);
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
        return side.getAxis() == getBlockState().getValue(WaterWheelBlock.AXIS);
    }
}

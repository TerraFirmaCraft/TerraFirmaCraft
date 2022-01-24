/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.LampBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.LampFuel;
import net.dries007.tfc.util.calendar.ICalendar;

public class LampBlockEntity extends TickCounterBlockEntity
{
    protected FluidTank tank;

    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);

    public LampBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.LAMP.get(), pos, state);
        this.tank = new FluidTank(TFCConfig.SERVER.lampCapacity.get(), stack -> LampFuel.get(stack.getFluid(), getBlockState()) != null);
    }

    @Nullable
    public LampFuel getFuel()
    {
        assert level != null;
        return LampFuel.get(tank.getFluid().getFluid(), level.getBlockState(getBlockPos()));
    }

    public boolean checkHasRanOut()
    {
        assert level != null;
        LampFuel fuel = getFuel();
        boolean ranOut = false;
        if (fuel != null)
        {
            int usage = Mth.floor(getTicksSinceUpdate() / (double) fuel.getBurnRate()); // burn rate is in mb/day
            if (usage >= 1)
            {
                FluidStack used = tank.drain(usage, IFluidHandler.FluidAction.EXECUTE);
                if (used.isEmpty() || used.getAmount() < usage)
                {
                    level.setBlockAndUpdate(getBlockPos(), level.getBlockState(getBlockPos()).setValue(LampBlock.LIT, false));
                    ranOut = true;
                }
                resetCounter();
            }
        }
        markForSync();
        return ranOut;
    }

    @Override
    public void loadAdditional(CompoundTag tag)
    {
        tank.readFromNBT(tag.getCompound("tank"));
        super.loadAdditional(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        tag.put("tank", tank.writeToNBT(new CompoundTag()));
        super.saveAdditional(tag);
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return holder.cast();
        return super.getCapability(capability, facing);
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.devices.LampBlock;
import net.dries007.tfc.common.capabilities.FluidTankCallback;
import net.dries007.tfc.common.capabilities.InventoryFluidTank;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.data.LampFuel;

public class LampBlockEntity extends TickCounterBlockEntity implements FluidTankCallback
{
    protected FluidTank tank;
    private final SidedHandler<IFluidHandler> sidedFluidInventory;
    private @Nullable LampFuel cachedFuel;

    public LampBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.LAMP.get(), pos, state);
        this.tank = new InventoryFluidTank(TFCConfig.SERVER.lampCapacity.get(), stack -> LampFuel.get(stack.getFluid(), getBlockState()) != null, this)
        {
            @Override
            public int fill(FluidStack resource, FluidAction action)
            {
                final int filled = super.fill(resource, action);
                if (filled > 0 && action.execute())
                {
                    lightAfterFilling();
                }
                return filled;
            }
        };
        this.sidedFluidInventory = new SidedHandler<>(tank);
    }

    @Override
    public void fluidTankChanged()
    {
        checkHasRanOut();
        markForSync();
    }

    @Nullable
    public LampFuel getFuel()
    {
        return LampFuel.get(tank.getFluid().getFluid(), getBlockState());
    }

    public int getFuelAmount()
    {
        return tank.getFluidAmount();
    }

    /**
     * Updates the lamp's current fuel value, resetting the counter since it was last updated. This may cause the lamp to
     * turn off, if it runs out of fuel completely.
     */
    public void checkHasRanOut()
    {
        assert level != null;

        final BlockState state = getBlockState();
        if (!state.getValue(LampBlock.LIT))
        {
            return;
        }

        @Nullable LampFuel fuel = getFuel();
        if (fuel == null)
        {
            // getFuel can also return null if called by a random tick during world close, but the LampFuel cache will be empty by that point
            // which turns the lamp off even if it has actual fuel in it.

            // This has to cover multiple cases:
            // - tank is empty = lamp is truly empty
            // - cachedFuel is not null = still fuel
            // - cachedFuel is null and the tank is not empty = the fluid in the lamp is not a fuel
            if (cachedFuel == null || tank.isEmpty())
            {
                // No fuel, so always unlit
                resetCounter();
                level.setBlockAndUpdate(worldPosition, state.setValue(LampBlock.LIT, false));
                return;
            }
            else
            {
                // Hopefully this is safe to use here
                fuel = cachedFuel;
            }
        }
        else
        {
            cachedFuel = fuel;
        }

        // Consume an appropriate amount of fuel based on how long the lamp has been since it last updated
        // N.B. The burn rate is in ticks / mB
        final int usage = Mth.floor(getTicksSinceUpdate() / (double) fuel.burnRate());
        if (usage >= 1)
        {
            resetCounter();
            markForSync();

            final FluidStack used = tank.drain(usage, IFluidHandler.FluidAction.EXECUTE);
            if (tank.isEmpty() || used.getAmount() < usage)
            {
                level.setBlockAndUpdate(worldPosition, state.setValue(LampBlock.LIT, false));
            }
        }
    }

    private void lightAfterFilling()
    {
        assert level != null;

        final BlockState state = getBlockState();
        final @Nullable LampFuel fuel = getFuel();
        if (!level.isClientSide && fuel != null && !state.getValue(LampBlock.LIT) && (state.getValue(LampBlock.POWERED) || fuel.burnRate() == -1))
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(LampBlock.LIT, true));
            resetCounter();
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        tank.readFromNBT(provider, tag.getCompound("tank"));
        cachedFuel = getFuel();
        super.loadAdditional(tag, provider);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        tag.put("tank", tank.writeToNBT(provider, new CompoundTag()));
        super.saveAdditional(tag, provider);
    }

    @Nullable
    public IFluidHandler getSidedFluidInventory(@Nullable Direction context)
    {
        return sidedFluidInventory.get(context);
    }
}

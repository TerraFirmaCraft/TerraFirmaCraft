/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

import net.dries007.tfc.util.calendar.Calendars;

public class TickCounterTileEntity extends TFCTileEntity
{
    private long lastUpdateTick = Integer.MIN_VALUE;

    public TickCounterTileEntity()
    {
        this(TFCTileEntities.TICK_COUNTER.get());
    }

    protected TickCounterTileEntity(TileEntityType<?> type)
    {
        super(type);
    }

    public long getTicksSinceUpdate()
    {
        return Calendars.SERVER.getTicks() - lastUpdateTick;
    }

    public void resetCounter()
    {
        lastUpdateTick = Calendars.SERVER.getTicks();
        markDirtyFast();
    }

    public void reduceCounter(long amount)
    {
        lastUpdateTick += amount;
        markDirtyFast();
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        lastUpdateTick = nbt.getLong("tick");
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putLong("tick", lastUpdateTick);
        return super.save(nbt);
    }
}

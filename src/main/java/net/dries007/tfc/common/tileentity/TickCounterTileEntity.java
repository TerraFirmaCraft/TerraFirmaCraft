/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.dries007.tfc.util.calendar.Calendars;

public class TickCounterTileEntity extends TFCTileEntity
{
    protected long lastUpdateTick = Integer.MIN_VALUE;

    public TickCounterTileEntity()
    {
        this(TFCTileEntities.TICK_COUNTER.get());
    }

    protected TickCounterTileEntity(BlockEntityType<?> type)
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
    public void load(BlockState state, CompoundTag nbt)
    {
        lastUpdateTick = nbt.getLong("tick");
        super.load(state, nbt);
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putLong("tick", lastUpdateTick);
        return super.save(nbt);
    }
}

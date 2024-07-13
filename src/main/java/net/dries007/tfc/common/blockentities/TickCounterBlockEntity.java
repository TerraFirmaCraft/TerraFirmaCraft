/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.calendar.Calendars;

public class TickCounterBlockEntity extends TFCBlockEntity
{
    public static void reset(Level level, BlockPos pos)
    {
        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
    }

    protected long lastUpdateTick = Integer.MIN_VALUE;

    public TickCounterBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.TICK_COUNTER.get(), pos, state);
    }

    protected TickCounterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public long getTicksSinceUpdate()
    {
        assert level != null;
        return Calendars.get(level).getTicks() - lastUpdateTick;
    }

    public void setLastUpdateTick(long tick)
    {
        lastUpdateTick = tick;
        setChanged();
    }

    public long getLastUpdateTick()
    {
        return lastUpdateTick;
    }

    public void resetCounter()
    {
        lastUpdateTick = Calendars.SERVER.getTicks();
        setChanged();
    }

    public void reduceCounter(long amount)
    {
        lastUpdateTick += amount;
        setChanged();
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        lastUpdateTick = nbt.getLong("tick");
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putLong("tick", lastUpdateTick);
        super.saveAdditional(nbt, provider);
    }
}

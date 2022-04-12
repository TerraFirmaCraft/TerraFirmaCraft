/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.plant.fruit.IBushBlock;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarTickable;

// todo: don't extend tick counter anymore
public class BerryBushBlockEntity extends TickCounterBlockEntity implements ICalendarTickable
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, BerryBushBlockEntity bush)
    {
        bush.checkForCalendarUpdate();
    }

    private long lastTick, lastUpdateTick;

    public BerryBushBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.BERRY_BUSH.get(), pos, state);
    }

    protected BerryBushBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        lastTick = lastUpdateTick = Calendars.SERVER.getTicks();
    }

    public void afterUpdate()
    {
        lastUpdateTick = Calendars.SERVER.getTicks();
    }

    public long getTicksSinceBushUpdate()
    {
        return Calendars.SERVER.getTicks() - lastUpdateTick;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        lastUpdateTick = nbt.getLong("lastUpdateTick");
        lastTick = nbt.getLong("lastTick");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putLong("lastUpdateTick", lastUpdateTick);
        nbt.putLong("lastTick", lastTick);
        super.saveAdditional(nbt);
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        if (level != null && ticks >= ICalendar.TICKS_IN_DAY)
        {
            final BlockState state = level.getBlockState(worldPosition);
            if (state.getBlock() instanceof IBushBlock bush)
            {
                bush.onUpdate(level, worldPosition, state);
            }
        }
    }

    @Override
    public long getLastUpdateTick()
    {
        return lastTick;
    }

    @Override
    public void setLastUpdateTick(long tick)
    {
        lastTick = tick;
    }
}

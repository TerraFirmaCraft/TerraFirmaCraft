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

import net.dries007.tfc.common.blocks.plant.fruit.IBushBlock;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarTickable;

public class BerryBushBlockEntity extends TFCBlockEntity implements ICalendarTickable
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, BerryBushBlockEntity bush)
    {
        bush.checkForCalendarUpdate();
    }

    private long lastTick; // The last tick this bush was ticked via the block entity's serverTick() method. A delta of > 1 is used to detect time skips
    private long lastUpdateTick; // The last tick the bush block was ticked via IBushBlock#onUpdate()

    public BerryBushBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.BERRY_BUSH.get(), pos, state);
    }

    protected BerryBushBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        lastTick = Integer.MIN_VALUE;
        lastUpdateTick = Calendars.SERVER.getTicks();
    }

    /**
     * @return The number of ticks since this bush block was ticked in {@link IBushBlock#onUpdate(Level, BlockPos, BlockState)}
     */
    public long getTicksSinceBushUpdate()
    {
        return Calendars.SERVER.getTicks() - lastUpdateTick;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        lastUpdateTick = nbt.getLong("lastUpdateTick");
        lastTick = nbt.getLong("lastTick");
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putLong("lastUpdateTick", lastUpdateTick);
        nbt.putLong("lastTick", lastTick);
        super.saveAdditional(nbt, provider);
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        if (level != null && ticks >= ICalendar.TICKS_IN_DAY)
        {
            final BlockState state = level.getBlockState(worldPosition);
            if (state.getBlock() instanceof IBushBlock bush)
            {
                bush.onUpdate(level, worldPosition, state); // Update the bush
                lastUpdateTick = Calendars.SERVER.getTicks(); // And the current time
                setChanged();
            }
        }
    }

    @Override
    @Deprecated
    public long getLastCalendarUpdateTick()
    {
        return lastTick;
    }

    @Override
    @Deprecated
    public void setLastCalendarUpdateTick(long tick)
    {
        lastTick = tick;
    }

    public void setLastBushTick(long ticks)
    {
        lastUpdateTick = ticks;
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.calendar.Calendar;
import net.dries007.tfc.util.calendar.Calendars;

import static net.dries007.tfc.util.calendar.ICalendar.*;

public class CalendarClockBlockEntity extends TickableBlockEntity
{
    private float monthAngle;
    private float minuteAngle;
    private float hourAngle;
    private int hour;
    private int month;
    private boolean needsUpdate = false;

    protected CalendarClockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public CalendarClockBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.CALENDAR_CLOCK.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CalendarClockBlockEntity clock)
    {
        if (clock.needsUpdate)
        {
            clock.markForSync();
            clock.needsUpdate = false;
        }
        if (level.getGameTime() % 40 == 0)
        {
            if (Calendars.SERVER.getAbsoluteCalendarMonthOfYear().ordinal() != clock.month)
            {
                clock.month = Calendars.SERVER.getAbsoluteCalendarMonthOfYear().ordinal();
            }
            if (getHourOfDay(Calendars.SERVER.getCalendarTicks()) != clock.hour)
            {
                clock.hour = getHourOfDay(Calendars.SERVER.getCalendarTicks());
            }
            level.updateNeighborsAt(pos, state.getBlock());
            level.updateNeighborsAt(pos.relative(state.getValue(BlockStateProperties.FACING).getOpposite()), state.getBlock());
        }
        clientTick(level, pos, state, clock);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CalendarClockBlockEntity clock)
    {
        if (level.getGameTime() % 20 == 0)
        {
            clock.minuteAngle = Mth.TWO_PI * Calendars.CLIENT.getCalendarFractionOfHour();
        }
        clock.hourAngle = Mth.TWO_PI * Calendars.CLIENT.getCalendarFractionOfDay() * 2;
        clock.monthAngle = Mth.TWO_PI * Calendars.CLIENT.getCalendarFractionOfYear();
    }

    public float[] getAngles()
    {
        return new float[] {minuteAngle, hourAngle, monthAngle};
    }

    public void needsInstantUpdate()
    {
        assert level != null;
        float fractionOver = (level.getGameTime() % 20f) / CALENDAR_TICKS_IN_HOUR;
        minuteAngle = Mth.TWO_PI * Calendars.CLIENT.getCalendarFractionOfHour() - Mth.TWO_PI * fractionOver + Mth.TWO_PI * 0.005f;
        hourAngle = Mth.TWO_PI * Calendars.CLIENT.getCalendarFractionOfDay() * 2;
        monthAngle = Mth.TWO_PI * Calendars.CLIENT.getCalendarFractionOfYear();
    }

    public int getRedstoneSignal()
    {
        if (this.getBlockState().getValue(TFCBlockStateProperties.CLOCK_MONTH_MODE))
        {
            return month;
        }
        return hour > 11
            ? hour - 12
            : hour;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.putFloat("minuteAngle", minuteAngle);
        tag.putFloat("hourAngle", hourAngle);
        tag.putFloat("monthAngle", monthAngle);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        hourAngle = tag.getFloat("hourAngle");
        minuteAngle = tag.getFloat("minuteAngle");
        monthAngle = tag.getFloat("monthAngle");
        needsUpdate = true;
    }
}

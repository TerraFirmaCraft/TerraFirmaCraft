/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.config.TFCConfig;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * This is the central tick tracking mechanism for all of TFC
 * Every server tick, the following statements are executed:
 * 1. ServerTick -> playerTime++
 * 2. ServerWorld#advanceTime -> dayTime++
 * 3. WorldTick -> calendarTime++
 * 4. (Possible) PlayerLoggedInEvent -> can update doDaylightCycle / arePlayersLoggedOn
 */
public class Calendar implements ICalendar
{
    public static final Logger LOGGER = LogUtils.getLogger();

    protected long playerTicks, calendarTicks;
    protected int daysInMonth;
    protected boolean doDaylightCycle, arePlayersLoggedOn;

    public Calendar()
    {
        resetToDefault();
    }

    @Override
    public long getTicks()
    {
        return playerTicks;
    }

    @Override
    public long getCalendarTicks()
    {
        return calendarTicks;
    }

    @Override
    public int getCalendarDaysInMonth()
    {
        return daysInMonth;
    }

    public CompoundTag write()
    {
        CompoundTag nbt = new CompoundTag();

        nbt.putInt("daysInMonth", daysInMonth);

        nbt.putLong("playerTime", playerTicks);
        nbt.putLong("calendarTime", calendarTicks);

        nbt.putBoolean("doDaylightCycle", doDaylightCycle);
        nbt.putBoolean("arePlayersLoggedOn", arePlayersLoggedOn);

        return nbt;
    }

    public void read(@Nullable CompoundTag nbt)
    {
        if (nbt != null)
        {
            daysInMonth = nbt.getInt("daysInMonth");

            playerTicks = nbt.getLong("playerTime");
            calendarTicks = nbt.getLong("calendarTime");

            doDaylightCycle = nbt.getBoolean("doDaylightCycle");
            arePlayersLoggedOn = nbt.getBoolean("arePlayersLoggedOn");
        }
    }

    public void write(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(daysInMonth);

        buffer.writeVarLong(playerTicks);
        buffer.writeVarLong(calendarTicks);

        buffer.writeBoolean(doDaylightCycle);
        buffer.writeBoolean(arePlayersLoggedOn);
    }

    public void read(FriendlyByteBuf buffer)
    {
        daysInMonth = buffer.readVarInt();

        playerTicks = buffer.readVarLong();
        calendarTicks = buffer.readVarLong();

        doDaylightCycle = buffer.readBoolean();
        arePlayersLoggedOn = buffer.readBoolean();
    }

    /**
     * Resets to the value of the provided calendar
     */
    public void resetTo(Calendar resetTo)
    {
        this.daysInMonth = resetTo.daysInMonth;

        this.playerTicks = resetTo.playerTicks;
        this.calendarTicks = resetTo.calendarTicks;

        this.doDaylightCycle = resetTo.doDaylightCycle;
        this.arePlayersLoggedOn = resetTo.arePlayersLoggedOn;
    }

    /**
     * Resets to default values
     */
    public void resetToDefault()
    {
        daysInMonth = TFCConfig.COMMON.defaultMonthLength.get();
        playerTicks = 0;
        calendarTicks = ((long) TFCConfig.COMMON.defaultCalendarStartDay.get() * ICalendar.TICKS_IN_DAY) + (6L * ICalendar.TICKS_IN_HOUR);
        doDaylightCycle = true;
        arePlayersLoggedOn = false;
    }
}
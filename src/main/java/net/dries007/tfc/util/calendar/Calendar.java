/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import net.dries007.tfc.config.TFCConfig;

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
    public static final Logger LOGGER = LogManager.getLogger();

    protected long playerTicks, calendarTicks;
    protected int daysInMonth;
    protected boolean doDaylightCycle, arePlayersLoggedOn;

    public Calendar()
    {
        daysInMonth = TFCConfig.COMMON.defaultMonthLength.get();
        playerTicks = 0;
        calendarTicks = (5 * daysInMonth * ICalendar.TICKS_IN_DAY) + (6 * ICalendar.TICKS_IN_HOUR);
        doDaylightCycle = true;
        arePlayersLoggedOn = false;
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

    public CompoundNBT write()
    {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putInt("daysInMonth", daysInMonth);

        nbt.putLong("playerTime", playerTicks);
        nbt.putLong("calendarTime", calendarTicks);

        nbt.putBoolean("doDaylightCycle", doDaylightCycle);
        nbt.putBoolean("arePlayersLoggedOn", arePlayersLoggedOn);

        return nbt;
    }

    public void read(@Nullable CompoundNBT nbt)
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

    public void write(PacketBuffer buffer)
    {
        buffer.writeVarInt(daysInMonth);

        buffer.writeVarLong(playerTicks);
        buffer.writeVarLong(calendarTicks);

        buffer.writeBoolean(doDaylightCycle);
        buffer.writeBoolean(arePlayersLoggedOn);
    }

    public void read(PacketBuffer buffer)
    {
        daysInMonth = buffer.readVarInt();

        playerTicks = buffer.readVarLong();
        calendarTicks = buffer.readVarLong();

        doDaylightCycle = buffer.readBoolean();
        arePlayersLoggedOn = buffer.readBoolean();
    }

    public void reset(Calendar resetTo)
    {
        this.daysInMonth = resetTo.daysInMonth;

        this.playerTicks = resetTo.playerTicks;
        this.calendarTicks = resetTo.calendarTicks;

        this.doDaylightCycle = resetTo.doDaylightCycle;
        this.arePlayersLoggedOn = resetTo.arePlayersLoggedOn;
    }
}
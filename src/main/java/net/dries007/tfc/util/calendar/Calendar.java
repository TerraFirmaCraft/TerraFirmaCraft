/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.dries007.tfc.config.TFCConfig;

/**
 * The shared implementation of {@link ICalendar}.
 *
 * @see Calendars
 */
public class Calendar implements ICalendar
{
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final StreamCodec<ByteBuf, Calendar> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, c -> c.daysInMonth,
        ByteBufCodecs.VAR_LONG, c -> c.playerTicks,
        ByteBufCodecs.VAR_LONG, c -> c.calendarTicks,
        ByteBufCodecs.BOOL, c -> c.doDaylightCycle,
        ByteBufCodecs.BOOL, c -> c.arePlayersLoggedOn,
        Calendar::new
    );

    protected long playerTicks, calendarTicks;
    protected int daysInMonth;
    protected boolean doDaylightCycle, arePlayersLoggedOn;

    public Calendar()
    {
        resetToDefault();
    }

    private Calendar(int daysInMonth, long playerTicks, long calendarTicks, boolean doDaylightCycle, boolean arePlayersLoggedOn)
    {
        this.daysInMonth = daysInMonth;
        this.playerTicks = playerTicks;
        this.calendarTicks = calendarTicks;
        this.doDaylightCycle = doDaylightCycle;
        this.arePlayersLoggedOn = arePlayersLoggedOn;
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
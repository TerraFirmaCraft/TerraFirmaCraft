/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.slf4j.Logger;

import net.dries007.tfc.config.TFCConfig;

/**
 * The shared implementation of {@link ICalendar}.
 *
 * @see Calendars
 */
public class Calendar implements ICalendar
{
    public static final int DEFAULT_MONTH_LENGTH = 8;

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final StreamCodec<ByteBuf, Calendar> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, c -> c.daysInMonth,
        ByteBufCodecs.VAR_LONG, c -> c.playerTicks,
        ByteBufCodecs.VAR_LONG, c -> c.calendarTicks,
        ByteBufCodecs.FLOAT, c -> c.calendarTickRate,
        ByteBufCodecs.FLOAT, c -> c.calendarPartialTick,
        ByteBufCodecs.BOOL, c -> c.arePlayersLoggedOn,
        Calendar::new
    );

    protected long playerTicks, calendarTicks;
    protected int daysInMonth;
    /** The tick rate (in {@code calendarTicks/playerTick}) of the calendar. Used for ticking and projections */
    protected float calendarTickRate;
    /** The fractional partial calendar tick that has accumulated since the last player tick. */
    protected float calendarPartialTick;
    /** {@code true} if either players are logged on (which forces calendar ticking), OR, we want to act as if they are (config-based). */
    protected boolean arePlayersLoggedOn;

    public Calendar()
    {
        resetToDefault();
    }

    private Calendar(int daysInMonth, long playerTicks, long calendarTicks, float calendarTickRate, float calendarPartialTick, boolean arePlayersLoggedOn)
    {
        this.daysInMonth = daysInMonth;
        this.playerTicks = playerTicks;
        this.calendarTicks = calendarTicks;
        this.calendarTickRate = calendarTickRate;
        this.calendarPartialTick = calendarPartialTick;
        this.arePlayersLoggedOn = arePlayersLoggedOn;
    }

    /**
     * Opens a calendar transaction, which allows you to safely manipulate time to perform a sequence of actions, without
     * possibility of distributing the state of the global calendar. Note that this should generally <strong>only</strong> be used
     * on {@link Calendars#SERVER}. The only case where this is useful to use on the client is during unit tests, where
     * the existing calendar will always be inferred to be on client.
     *
     * @return A new {@link CalendarTransaction}
     * @see CalendarTransaction
     */
    @Override
    public CalendarTransaction transaction()
    {
        return new Transaction();
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
        final CompoundTag nbt = new CompoundTag();
        nbt.putInt("daysInMonth", daysInMonth);
        nbt.putLong("playerTime", playerTicks);
        nbt.putLong("calendarTime", calendarTicks);
        nbt.putFloat("calendarTickRate", calendarTickRate);
        nbt.putFloat("calendarPartialTick", calendarPartialTick);
        nbt.putBoolean("arePlayersLoggedOn", arePlayersLoggedOn);
        return nbt;
    }

    public void read(CompoundTag nbt)
    {
        daysInMonth = nbt.getInt("daysInMonth");
        playerTicks = nbt.getLong("playerTime");
        calendarTicks = nbt.getLong("calendarTime");
        calendarTickRate = nbt.getFloat("calendarTickRate");
        calendarPartialTick = nbt.getFloat("calendarPartialTick");
        arePlayersLoggedOn = nbt.getBoolean("arePlayersLoggedOn");
    }

    /**
     * Resets to the value of the provided calendar
     */
    public void resetTo(Calendar other)
    {
        this.daysInMonth = other.daysInMonth;
        this.playerTicks = other.playerTicks;
        this.calendarTicks = other.calendarTicks;
        this.calendarTickRate = other.calendarTickRate;
        this.calendarPartialTick = other.calendarPartialTick;
        this.arePlayersLoggedOn = other.arePlayersLoggedOn;
    }

    /**
     * Resets to default values
     */
    public void resetToDefault()
    {
        daysInMonth = TFCConfig.COMMON.defaultMonthLength.get();
        playerTicks = 0;
        calendarTicks = ((long) TFCConfig.COMMON.defaultCalendarStartDay.get() * ICalendar.TICKS_IN_DAY) + (6L * ICalendar.TICKS_IN_HOUR);
        calendarTickRate = 20f / 24f;
        calendarPartialTick = 0f;
        arePlayersLoggedOn = false;
    }

    final class Transaction implements CalendarTransaction
    {
        private final long originalPlayerTicks = playerTicks;

        @Override
        public void add(long ticks)
        {
            Calendar.this.playerTicks += ticks;
        }

        @Override
        public long ticks()
        {
            return Calendar.this.playerTicks - originalPlayerTicks;
        }

        @Override
        public void close()
        {
            Calendar.this.playerTicks = originalPlayerTicks;
        }
    }
}
/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import io.netty.buffer.ByteBuf;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketCalendarUpdate;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CalendarTFC implements INBTSerializable<NBTTagCompound>
{
    public static final String[] DAY_NAMES = new String[] {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    public static final Map<String, String> BIRTHDAYS = new HashMap<>();
    /* The offset in ticks between the world time (sun position) and the calendar (since world time 0 = 6 AM) */
    public static final int WORLD_TIME_OFFSET = 6 * ICalendar.TICKS_IN_HOUR;

    /* The internal calendar instance. Do NOT use this to calculate times. */
    public static final CalendarTFC INSTANCE = new CalendarTFC();

    /**
     * Total time. Always returns the result of {@link World#getTotalWorldTime()}
     */
    public static final ICalendar TOTAL_TIME = () -> CalendarTFC.INSTANCE.worldTotalTime;

    /**
     * Player time. Advances when player sleeps, stops when no players are online
     * NOT synced with the daylight cycle.
     */
    public static final ICalendar PLAYER_TIME = () -> CalendarTFC.INSTANCE.playerTime;

    /**
     * Calendar time. Advances when player sleeps, stops when doDaylightCycle is false
     * Synced with the daylight cycle
     * Players can see this via the calendar GUI tab
     * Calendar Time 0 = Midnight, January 1, 1000
     */
    public static final ICalendarFormatted CALENDAR_TIME = new ICalendarFormatted()
    {
        @Override
        public long getTicks()
        {
            return CalendarTFC.INSTANCE.calendarTime;
        }

        @Override
        public long getDaysInMonth()
        {
            return CalendarTFC.INSTANCE.daysInMonth;
        }
    };

    public static final int DEFAULT_DAYS_IN_MONTH = 8;
    public static final int DEFAULT_CALENDAR_TIME_OFFSET = (6 * DEFAULT_DAYS_IN_MONTH * ICalendar.TICKS_IN_DAY) + (6 * ICalendar.TICKS_IN_HOUR);

    static
    {
        // Original developers, all hail their glorious creation
        BIRTHDAYS.put("JULY7", "Bioxx's Birthday");
        BIRTHDAYS.put("JUNE18", "Kitty's Birthday");
        BIRTHDAYS.put("OCTOBER2", "Dunk's Birthday");

        // 1.12+ Dev Team and significant contributors
        BIRTHDAYS.put("MAY1", "Dries's Birthday");
        BIRTHDAYS.put("DECEMBER9", "Alcatraz's Birthday");
        BIRTHDAYS.put("FEBRUARY31", "Bunsan's Birthday");
        BIRTHDAYS.put("MARCH14", "Claycorp's Birthday");
        BIRTHDAYS.put("DECEMBER1", "LightningShock's Birthday");
        BIRTHDAYS.put("JANUARY20", "Therighthon's Birthday");
        BIRTHDAYS.put("FEBRUARY21", "CtrlAltDavid's Birthday");
        BIRTHDAYS.put("MARCH10", "Disastermoo's Birthday");
    }

    private long worldTotalTime, playerTime, calendarTime;
    private long playerTimeOffset, calendarTimeOffset;
    private int daysInMonth;
    private boolean doDaylightCycle, arePlayersLoggedOn;

    public CalendarTFC()
    {
        // Initialize to default values
        daysInMonth = DEFAULT_DAYS_IN_MONTH;
        playerTimeOffset = 0;
        calendarTimeOffset = DEFAULT_CALENDAR_TIME_OFFSET;
        doDaylightCycle = true;
        arePlayersLoggedOn = false;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setInteger("daysInMonth", daysInMonth);

        nbt.setLong("worldTotalTime", worldTotalTime);
        nbt.setLong("playerTimeOffset", playerTimeOffset);
        nbt.setLong("calendarTimeOffset", calendarTimeOffset);

        nbt.setBoolean("doDaylightCycle", doDaylightCycle);
        nbt.setBoolean("arePlayersLoggedOn", arePlayersLoggedOn);

        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            daysInMonth = nbt.getInteger("daysInMonth");

            worldTotalTime = nbt.getLong("worldTotalTime");
            playerTimeOffset = nbt.getLong("playerTimeOffset");
            calendarTimeOffset = nbt.getLong("calendarTimeOffset");

            doDaylightCycle = nbt.getBoolean("doDaylightCycle");
            arePlayersLoggedOn = nbt.getBoolean("arePlayersLoggedOn");

            // Re-calculate values
            playerTime = worldTotalTime + playerTimeOffset;
            calendarTime = worldTotalTime + calendarTimeOffset;
        }
    }

    public void write(ByteBuf buffer)
    {
        buffer.writeInt(daysInMonth);

        buffer.writeLong(worldTotalTime);
        buffer.writeLong(playerTimeOffset);
        buffer.writeLong(calendarTimeOffset);

        buffer.writeBoolean(doDaylightCycle);
        buffer.writeBoolean(arePlayersLoggedOn);
    }

    public void read(ByteBuf buffer)
    {
        daysInMonth = buffer.readInt();

        worldTotalTime = buffer.readLong();
        playerTimeOffset = buffer.readLong();
        calendarTimeOffset = buffer.readLong();

        doDaylightCycle = buffer.readBoolean();
        arePlayersLoggedOn = buffer.readBoolean();

        // Re-calculate values
        playerTime = worldTotalTime + playerTimeOffset;
        calendarTime = worldTotalTime + calendarTimeOffset;
    }

    public void reset(CalendarTFC resetTo)
    {
        this.daysInMonth = resetTo.daysInMonth;

        this.worldTotalTime = resetTo.worldTotalTime;
        this.playerTimeOffset = resetTo.playerTimeOffset;
        this.calendarTimeOffset = resetTo.calendarTimeOffset;

        this.doDaylightCycle = resetTo.doDaylightCycle;
        this.arePlayersLoggedOn = resetTo.arePlayersLoggedOn;

        // Re-calculate values
        playerTime = worldTotalTime + playerTimeOffset;
        calendarTime = worldTotalTime + calendarTimeOffset;
    }

    public int getDaysInMonth()
    {
        return daysInMonth;
    }

    /**
     * Sends an update packet to a player on log in
     *
     * @param player the server player to send to
     */
    public void updatePlayer(EntityPlayerMP player)
    {
        TerraFirmaCraft.getNetwork().sendTo(new PacketCalendarUpdate(this), player);
    }

    /**
     * Sets the per-world month length. Will also recalculate all time that has passed so far
     *
     * @param world       The current world
     * @param daysInMonth the days per each month
     */
    public void setMonthLength(World world, int daysInMonth)
    {
        // Current amount of months and remainder - these will stay the same
        long totalMonths = CalendarTFC.CALENDAR_TIME.getTotalMonths();
        long remainder = calendarTime - (totalMonths * this.daysInMonth * ICalendar.TICKS_IN_DAY);

        // New calendar time based on the same amount of months + remainder
        this.daysInMonth = daysInMonth;
        long newCalendarTime = (totalMonths * daysInMonth * ICalendar.TICKS_IN_DAY) + remainder;

        // Reset and update
        setCalendarTime(world, newCalendarTime);
    }

    /**
     * Called to set the total world time from tick events
     *
     * @param worldTotalTime the total world time
     */
    public void setTotalTime(long worldTotalTime)
    {
        // Set total world time directly
        if (worldTotalTime < 0)
        {
            worldTotalTime = 0;
        }
        this.worldTotalTime = worldTotalTime;

        // Set player time based on if time is advancing or not
        if (arePlayersLoggedOn)
        {
            playerTime = worldTotalTime + playerTimeOffset;
        }
        else
        {
            playerTimeOffset = playerTime - worldTotalTime;
        }

        // Set calendar time based if time is advancing or not
        if (doDaylightCycle)
        {
            calendarTime = worldTotalTime + calendarTimeOffset;
        }
        else
        {
            calendarTimeOffset = calendarTime - worldTotalTime;
        }
    }

    /**
     * Sets the player time by resetting the offset.
     * This should only be used for debugging purposes
     *
     * @param world      The world
     * @param playerTime The calendar time
     */
    public void setPlayerTime(World world, long playerTime)
    {
        if (playerTime < this.playerTime || playerTime < 0)
        {
            TerraFirmaCraft.getLog().warn("Something tried to set the player time to go in reverse! This should never happen!");
        }
        else
        {
            // Don't set the player time directly, instead set the offset from the total time
            this.playerTimeOffset = playerTime - worldTotalTime;
            this.playerTime = worldTotalTime + playerTimeOffset;

            // Then update world data + clients
            updateWorldDataAndSync(world);
        }
    }

    /**
     * Sets the calendar time.
     * This actually sets the calendar offset - i.e. the difference between the current world time and the calendar time
     * It then will save that offset to world data, and sync to client
     *
     * @param world        The world
     * @param calendarTime The calendar time
     */
    public void setCalendarTime(World world, long calendarTime)
    {
        // Calendar time is not allowed to be negative (i.e. before January 1, 1000)
        if (calendarTime < 0)
        {
            calendarTime = 0;
            TerraFirmaCraft.getLog().warn("Something tried to set the calendar time to a negative value! This should never happen!");
        }
        // Don't set the calendar time directly, instead set the offset from the total time
        this.calendarTimeOffset = calendarTime - worldTotalTime;
        this.calendarTime = worldTotalTime + calendarTimeOffset;

        // Then update world data + clients
        updateWorldDataAndSync(world);
    }

    public void setDoDaylightCycle(World world, boolean doDaylightCycle)
    {
        this.doDaylightCycle = doDaylightCycle;

        // Then update world data + clients
        updateWorldDataAndSync(world);
    }

    public void setArePlayersLoggedOn(World world, boolean arePlayersLoggedOn)
    {
        this.arePlayersLoggedOn = arePlayersLoggedOn;

        // Then update world data + clients
        updateWorldDataAndSync(world);
    }

    public void updateWorldDataAndSync(World world)
    {
        // Update world data
        CalendarWorldData data = CalendarWorldData.get(world);
        data.instance.reset(this);
        data.markDirty();

        // Sync to clients
        if (!world.isRemote)
        {
            TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(this));
        }
    }
}

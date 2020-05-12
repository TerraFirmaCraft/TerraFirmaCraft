/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketCalendarUpdate;

public final class CalendarTFC implements INBTSerializable<NBTTagCompound>
{
    public static final CalendarTFC INSTANCE = new CalendarTFC();

    /**
     * Player time. Advances when player sleeps, stops when no players are online
     * NOT synced with the daylight cycle.
     * Used for almost everything that tracks time.
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

    public static final String[] DAY_NAMES = new String[] {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    public static final Map<String, String> BIRTHDAYS = new HashMap<>();

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

    /**
     * This runs a sequence of code, but first will set the calendar and player time by an offset
     * Useful if we need to run code that technically needs to happen at a different calendar time
     * The offsets are removed once the transaction is complete
     *
     * @param transactionPlayerTimeOffset   the offset to be added to the player time
     * @param transactionCalendarTimeOffset the offset to be added to the calendar time
     */
    public static void runTransaction(long transactionPlayerTimeOffset, long transactionCalendarTimeOffset, Runnable transaction)
    {
        try
        {
            INSTANCE.playerTime += transactionPlayerTimeOffset;
            INSTANCE.calendarTime += transactionCalendarTimeOffset;

            transaction.run();
        }
        finally
        {
            // Always reset after transaction complete
            INSTANCE.playerTime -= transactionPlayerTimeOffset;
            INSTANCE.calendarTime -= transactionCalendarTimeOffset;
        }
    }

    private long playerTime, calendarTime;
    private int daysInMonth;
    private boolean doDaylightCycle, arePlayersLoggedOn;
    private MinecraftServer server;

    public CalendarTFC()
    {
        // Initialize to default values
        daysInMonth = ConfigTFC.General.MISC.defaultMonthLength;
        playerTime = 0;
        calendarTime = (5 * daysInMonth * ICalendar.TICKS_IN_DAY) + (6 * ICalendar.TICKS_IN_HOUR);
        doDaylightCycle = true;
        arePlayersLoggedOn = false;
    }

    public void setTimeFromCalendarTime(long calendarTimeToSetTo)
    {
        // Calculate the time jump
        long timeJump = calendarTimeToSetTo - calendarTime;

        calendarTime = calendarTimeToSetTo;
        playerTime += timeJump;

        // Update the actual world times
        for (World world : server.worlds)
        {
            long currentWorldTime = world.getWorldTime();
            world.setWorldTime(currentWorldTime + timeJump);
        }

        TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(this));
    }

    /**
     * Jumps the calendar ahead to a world time.
     * This does not automatically fix sync errors
     *
     * @param worldTimeToSetTo a world time, obtained from {@link World#getWorldTime()}. Must be in [0, ICalendar.TICKS_IN_DAY]
     */
    public long setTimeFromWorldTime(long worldTimeToSetTo)
    {
        // Calculate the offset to jump to
        long worldTimeJump = (worldTimeToSetTo % ICalendar.TICKS_IN_DAY) - CalendarTFC.CALENDAR_TIME.getWorldTime();
        if (worldTimeJump < 0)
        {
            worldTimeJump += ICalendar.TICKS_IN_DAY;
        }

        calendarTime += worldTimeJump;
        playerTime += worldTimeJump;

        TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(this));
        return worldTimeJump;
    }

    /**
     * For serialization when saving to world data
     */
    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setInteger("daysInMonth", daysInMonth);

        nbt.setLong("playerTime", playerTime);
        nbt.setLong("calendarTime", calendarTime);

        nbt.setBoolean("doDaylightCycle", doDaylightCycle);
        nbt.setBoolean("arePlayersLoggedOn", arePlayersLoggedOn);

        return nbt;
    }

    /**
     * For serialization when reading from world data
     */
    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            daysInMonth = nbt.getInteger("daysInMonth");

            playerTime = nbt.getLong("playerTime");
            calendarTime = nbt.getLong("calendarTime");

            doDaylightCycle = nbt.getBoolean("doDaylightCycle");
            arePlayersLoggedOn = nbt.getBoolean("arePlayersLoggedOn");
        }
    }

    public void write(ByteBuf buffer)
    {
        buffer.writeInt(daysInMonth);

        buffer.writeLong(playerTime);
        buffer.writeLong(calendarTime);

        buffer.writeBoolean(doDaylightCycle);
        buffer.writeBoolean(arePlayersLoggedOn);
    }

    public void read(ByteBuf buffer)
    {
        daysInMonth = buffer.readInt();

        playerTime = buffer.readLong();
        calendarTime = buffer.readLong();

        doDaylightCycle = buffer.readBoolean();
        arePlayersLoggedOn = buffer.readBoolean();
    }

    public void resetTo(CalendarTFC resetTo)
    {
        this.daysInMonth = resetTo.daysInMonth;

        this.playerTime = resetTo.playerTime;
        this.calendarTime = resetTo.calendarTime;

        this.doDaylightCycle = resetTo.doDaylightCycle;
        this.arePlayersLoggedOn = resetTo.arePlayersLoggedOn;
    }

    /**
     * Called from {@link net.minecraftforge.fml.common.event.FMLServerStartingEvent}
     * Initializes the calendar with the current minecraft server instance, reloading all values from world saved data
     */
    public void init(MinecraftServer server)
    {
        this.server = server;

        // Initialize doDaylightCycle to false as the server is just starting
        server.getEntityWorld().getGameRules().setOrCreateGameRule("doDaylightCycle", "false");

        resetTo(CalendarWorldData.get(server.getEntityWorld()).getCalendar());
        TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(this));
    }

    /**
     * Called on server ticks, syncs to client
     */
    public void onServerTick()
    {
        if (arePlayersLoggedOn)
        {
            playerTime++;
        }
        if (server.getTickCounter() % 10 == 0)
        {
            TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(this));
        }
    }

    /**
     * Called on each overworld tick, increments and syncs calendar time
     */
    public void onOverworldTick(World world)
    {
        if (doDaylightCycle && arePlayersLoggedOn)
        {
            calendarTime++;
        }
        long deltaWorldTime = (world.getWorldTime() % ICalendar.TICKS_IN_DAY) - CALENDAR_TIME.getWorldTime();
        if (deltaWorldTime > 1 || deltaWorldTime < -1)
        {
            TerraFirmaCraft.getLog().info("World time and Calendar Time are out of sync! Trying to fix...");
            TerraFirmaCraft.getLog().info("Calendar Time = {} ({}), Player Time = {}, World Time = {}, doDaylightCycle = {}, ArePlayersLoggedOn = {}", calendarTime, CALENDAR_TIME.getWorldTime(), playerTime, world.getWorldTime() % ICalendar.TICKS_IN_DAY, doDaylightCycle, arePlayersLoggedOn);

            // Check if tracking values are wrong
            boolean checkArePlayersLoggedOn = server.getPlayerList().getPlayers().size() > 0;
            if (arePlayersLoggedOn != checkArePlayersLoggedOn)
            {
                // Whoops, somehow we missed this.
                TerraFirmaCraft.getLog().info("Setting ArePlayersLoggedOn = {}", checkArePlayersLoggedOn);
                setPlayersLoggedOn(checkArePlayersLoggedOn);
            }
            if (deltaWorldTime < 0)
            {
                // Calendar is ahead, so jump world time
                world.setWorldTime(world.getWorldTime() - deltaWorldTime);
                TerraFirmaCraft.getLog().info("Calendar is ahead by {} ticks, jumping world time to catch up", -deltaWorldTime);
            }
            else
            {
                // World time is ahead, so jump calendar
                calendarTime += deltaWorldTime;
                TerraFirmaCraft.getLog().info("Calendar is behind by {} ticks, jumping calendar time to catch up", deltaWorldTime);
            }
            TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(this));
        }
    }

    public void setMonthLength(int newMonthLength)
    {
        // Recalculate the new calendar time
        // Preserve the current month, time of day, and position within the month
        long baseMonths = CalendarTFC.CALENDAR_TIME.getTotalMonths();
        long baseDayTime = calendarTime - (CalendarTFC.CALENDAR_TIME.getTotalDays() * ICalendar.TICKS_IN_DAY);
        // Minus one here because `getDayOfMonth` returns the player visible one (which adds one)
        float monthPercent = (float) (CalendarTFC.CALENDAR_TIME.getDayOfMonth() - 1) / daysInMonth;
        int newDayOfMonth = (int) (monthPercent * newMonthLength);

        this.daysInMonth = newMonthLength;
        this.calendarTime = (baseMonths * daysInMonth + newDayOfMonth) * ICalendar.TICKS_IN_DAY + baseDayTime;

        TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(this));
    }

    public void setPlayersLoggedOn(boolean arePlayersLoggedOn)
    {
        GameRules rules = server.getEntityWorld().getGameRules();
        this.arePlayersLoggedOn = arePlayersLoggedOn;
        if (arePlayersLoggedOn)
        {
            rules.setOrCreateGameRule("doDaylightCycle", Boolean.toString(doDaylightCycle));
            TerraFirmaCraft.getLog().info("Reverted doDaylightCycle to {} as players are logged in.", doDaylightCycle);
        }
        else
        {
            rules.setOrCreateGameRule("doDaylightCycle", Boolean.toString(false));
            TerraFirmaCraft.getLog().info("Forced doDaylightCycle to false as no players are logged in. Will revert to {} as soon as a player logs in.", doDaylightCycle);
        }

        TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(this));
    }

    public void setDoDaylightCycle()
    {
        GameRules rules = server.getEntityWorld().getGameRules();
        this.doDaylightCycle = rules.getBoolean("doDaylightCycle");
        if (!arePlayersLoggedOn)
        {
            rules.setOrCreateGameRule("doDaylightCycle", "false");
            TerraFirmaCraft.getLog().info("Forced doDaylightCycle to false as no players are logged in. Will revert to {} as soon as a player logs in.", doDaylightCycle);
        }

        TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(this));
    }
}

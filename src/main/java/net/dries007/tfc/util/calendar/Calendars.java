/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import net.dries007.tfc.client.ClientCalendar;

/**
 * This is the central tick tracking mechanism for all of TFC. Calendars can be accessed directly, if the logical side is known,
 * or through a {@link LevelReader}, or through {@link #get()} if one is not available.
 * <p>
 * Every server tick, the following statements are executed in order:
 * <ol>
 *     <li>{@link CalendarEventHandler#onServerTick(ServerTickEvent.Pre) onServerTick()} increments the {@code playerTick} count of the TFC calendar</li>
 *     <li>{@link ServerLevel#tickTime() tickTime()} increments the {@code dayTime} count of the server</li>
 *     <li>{@link CalendarEventHandler#onOverworldTick(LevelTickEvent.Post) onOverworldTick()} of the overworld increments the {@code calendarTick} count of the TFC calendar, if the daylight cycle is not paused</li>
 *     <li>{@link CalendarEventHandler#onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent) onPlayerLoggedIn()} or the inverse may fire, which may adjust if players are logged in, or the daylight cycle.</li>
 * </ol>
 * There are two separate tick counts implemented by the TFC calendar:
 * <ul>
 *     <li>{@code playerTick}s are a <strong>monotonic, increasing</strong> calendar. They are global across dimensions and not synced to any daylight cycle. These should be used for saving timestamps.</li>
 *     <li>{@code calendarTick}s are a representation of the overworld seasonal and daytime calendar. They may stop (i.e. if the daylight cycle is paused), or reverse (if the month or day length is modified). <strong>Do not store these in timestamps!</strong></li>
 * </ul>
 * <strong>N.B.</strong> When storing any time stamp, only store the result of {@link ICalendar#getTicks()} - not calendar ticks, or any derivative
 * of ticks such as number of days, as those may not be known to be stable.
 */
public final class Calendars
{
    /**
     * These are separated into LOGICAL sides.
     * References must make sure they are choosing the correct side for their application
     */
    public static final ServerCalendar SERVER = new ServerCalendar();
    public static final ClientCalendar CLIENT = new ClientCalendar();

    /**
     * @return The calendar for the same logical side as the {@code level}
     */
    public static ICalendar get(LevelReader level)
    {
        return level.isClientSide() ? CLIENT : SERVER;
    }

    /**
     * @return The calendar for the requested logical side
     */
    public static ICalendar get(boolean isClientSide)
    {
        return isClientSide ? CLIENT : SERVER;
    }

    /**
     * @return The calendar for the correct logical side making the best guess about the current calling context.
     * @implNote This will always return the server calendar on a physical client, which should be fine as they will be synchronized in such a case.
     */
    public static ICalendar get()
    {
        return get(isClientSide());
    }

    private static boolean isClientSide()
    {
        return ServerLifecycleHooks.getCurrentServer() == null || SERVER.getTicks() == 0;
    }

    private Calendars() {}
}
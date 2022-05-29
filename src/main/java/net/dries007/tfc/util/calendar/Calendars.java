/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.world.level.LevelReader;
import net.minecraftforge.server.ServerLifecycleHooks;

import net.dries007.tfc.client.ClientCalendar;
import net.dries007.tfc.util.Helpers;

/**
 * This is the central tick tracking mechanism for all of TFC
 * Every server tick, the following statements are executed in order:
 * 1. ServerTick -> playerTime++
 * 2. ServerWorld#advanceTime -> dayTime++
 * 3. WorldTick -> calendarTime++
 * 4. (Possible) PlayerLoggedInEvent -> can update doDaylightCycle / arePlayersLoggedOn
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
     * Gets the correct calendar for the current world context
     */
    public static ICalendar get(LevelReader world)
    {
        return Helpers.isClientSide(world) ? CLIENT : SERVER;
    }

    public static ICalendar get(boolean isClientSide)
    {
        return isClientSide ? CLIENT : SERVER;
    }

    /**
     * Makes a best guess about which calendar is valid based on the current tick values
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
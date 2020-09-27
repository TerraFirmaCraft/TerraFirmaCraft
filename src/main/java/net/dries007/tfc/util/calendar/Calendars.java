/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.world.IWorldReader;

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
    public static ICalendar get(IWorldReader world)
    {
        return Helpers.isRemote(world) ? CLIENT : SERVER;
    }

    private Calendars() {}
}
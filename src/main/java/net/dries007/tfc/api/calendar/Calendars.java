/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.calendar;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.GameRules;
import net.minecraft.world.IWorldReader;

import net.dries007.tfc.client.ClientCalendar;
import net.dries007.tfc.util.ReentrantRunnable;
import net.dries007.tfc.util.calendar.ServerCalendar;

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

    // todo: move below and other buisness to some other place - day names and birthdays goes to translation keys / client calendar, and setup and gamerule goes to server calendar
    public static final String[] DAY_NAMES = new String[] {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    public static final Map<String, String> BIRTHDAYS = new HashMap<>();

    public static final ReentrantRunnable DO_DAYLIGHT_CYCLE = new ReentrantRunnable(SERVER::setDoDaylightCycle);

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
     * Gets the correct calendar for the current world context
     */
    public static ICalendar get(IWorldReader world)
    {
        return world.isRemote() ? CLIENT : SERVER;
    }

    @SuppressWarnings("unchecked")
    public static void setup()
    {
        GameRules.RuleType<GameRules.BooleanValue> type = (GameRules.RuleType<GameRules.BooleanValue>) GameRules.GAME_RULES.get(GameRules.DO_DAYLIGHT_CYCLE);
        type.changeListener = type.changeListener.andThen((server, t) -> DO_DAYLIGHT_CYCLE.run());
    }
}

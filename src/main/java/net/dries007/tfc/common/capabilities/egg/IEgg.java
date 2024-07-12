/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.egg;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.util.calendar.Calendars;

public interface IEgg
{
    /**
     * @return the day it will hatch, as in ICalendar#getTotalDays
     */
    long getHatchDay();

    /**
     * @return the Entity that is hatched from this egg, or null if none
     */
    Optional<Entity> getEntity(Level level);

    /**
     * Is this egg fertilized?
     */
    boolean isFertilized();

    /**
     * Fertilizes this egg, setting what entity and which day this egg will hatch
     *
     * @param entity   the entity this egg's gonna hatch
     * @param hatchDay the hatch day, as in ICalendar#getTotalDays
     */
    void setFertilized(@NotNull Entity entity, long hatchDay);

    /**
     * Clears the data from the stack
     */
    default void removeFertilization()
    {

    }

    default void addTooltipInfo(@NotNull List<Component> text)
    {
        if (isFertilized())
        {
            final long remainingDays = getHatchDay() - Calendars.CLIENT.getTotalDays();
            text.add(Component.translatable("tfc.tooltip.fertilized"));
            if (remainingDays > 0)
            {
                text.add(Component.translatable("tfc.tooltip.egg_hatch", remainingDays));
            }
            else
            {
                text.add(Component.translatable("tfc.tooltip.egg_hatch_today"));
            }
        }
    }
}

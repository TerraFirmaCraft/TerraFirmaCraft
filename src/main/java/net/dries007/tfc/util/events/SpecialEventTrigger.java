/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.TickTrigger;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.util.Helpers;

public class SpecialEventTrigger extends TickTrigger
{
    public static void registerSpecialEventTriggers()
    {
        CriteriaTriggers.register(FULL_POWDERKEG);
        CriteriaTriggers.register(FULL_FERTILIZER);
        CriteriaTriggers.register(LAVA_LAMP);
        SpecialEntityTrigger.registerSpecialEntityTriggers();
        SpecialBlockTrigger.registerSpecialBlockTriggers();
    }

    public static final SpecialEventTrigger FULL_POWDERKEG = new SpecialEventTrigger(Helpers.identifier("full_powderkeg"));
    public static final SpecialEventTrigger FULL_FERTILIZER = new SpecialEventTrigger(Helpers.identifier("full_fertilizer"));
    public static final SpecialEventTrigger LAVA_LAMP = new SpecialEventTrigger(Helpers.identifier("lava_lamp"));

    private final ResourceLocation id;

    public SpecialEventTrigger(ResourceLocation id)
    {
        this.id = id;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }
}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.heat;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.Helpers;

public final class CapabilityItemHeat
{
    @CapabilityInject(IItemHeat.class)
    public static final Capability<IItemHeat> ITEM_HEAT_CAPABILITY = Helpers.getNull();

    public static final float MIN_TEMPERATURE = 0f;
    /**
     * For most practical purposes this is the max temperature than an item should reach.
     * i.e. all metals should melt either before this, or never.
     */
    public static final float MAX_TEMPERATURE = 1601f;

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IItemHeat.class, new DumbStorage<>(), ItemHeatHandler::new);
    }

    /**
     * Call this from within {@link IItemHeat#getTemperature()}
     */
    public static float adjustTemp(float temp, float heatCapacity, long ticksSinceUpdate)
    {
        if (ticksSinceUpdate <= 0) return temp;
        final float newTemp = temp - heatCapacity * (float) ticksSinceUpdate * (float) ConfigTFC.GENERAL.temperatureModifierGlobal;
        return newTemp < MIN_TEMPERATURE ? MIN_TEMPERATURE : newTemp;
    }

    public static void addTemp(IItemHeat instance)
    {
        // Default modifier = 3 (2x normal cooling)
        addTemp(instance, 3);
    }

    /**
     * Use this to increase the heat on an IItemHeat instance.
     *
     * @param modifier the modifier for how much this will heat up: 0 - 1 slows down cooling, 1 = no heating or cooling, > 1 heats, 2 heats at the same rate of normal cooling, 2+ heats faster
     */
    public static void addTemp(IItemHeat instance, float modifier)
    {
        final float temp = instance.getTemperature() + modifier * instance.getHeatCapacity() * (float) ConfigTFC.GENERAL.temperatureModifierGlobal;
        instance.setTemperature(temp > MAX_TEMPERATURE ? MAX_TEMPERATURE : temp);
    }
}

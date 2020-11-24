/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.capabilities.heat;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class HeatCapability
{
    @CapabilityInject(IHeat.class)
    public static final Capability<IHeat> CAPABILITY = Helpers.notNull();
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "item_heat");

    public static void setup()
    {
        Helpers.registerSimpleCapability(IHeat.class);
    }

    /**
     * Helper method to adjust temperature towards a value, without overshooting or stuttering
     */
    public static float adjustTempTowards(float temp, float target, float delta)
    {
        return adjustTempTowards(temp, target, delta, delta);
    }

    public static float adjustTempTowards(float temp, float target, float deltaPositive, float deltaNegative)
    {
        if (temp < target)
        {
            return Math.min(temp + deltaPositive, target);
        }
        else if (temp > target)
        {
            return Math.max(temp - deltaNegative, target);
        }
        else
        {
            return target;
        }
    }

    /**
     * Call this from within {@link IHeat#getTemperature()}
     */
    public static float adjustTemp(float temp, float heatCapacity, long ticksSinceUpdate)
    {
        if (ticksSinceUpdate <= 0) return temp;
        final float newTemp = temp - heatCapacity * (float) (ticksSinceUpdate * TFCConfig.SERVER.itemHeatingModifier.get());
        return newTemp < 0 ? 0 : newTemp;
    }

    public static void addTemp(IHeat instance)
    {
        // Default modifier = 3 (2x normal cooling)
        addTemp(instance, 3);
    }

    /**
     * Use this to increase the heat on an IItemHeat instance.
     *
     * @param modifier the modifier for how much this will heat up: 0 - 1 slows down cooling, 1 = no heating or cooling, > 1 heats, 2 heats at the same rate of normal cooling, 2+ heats faster
     */
    public static void addTemp(IHeat instance, float modifier)
    {
        final float temp = instance.getTemperature() + modifier * instance.getHeatCapacity() * TFCConfig.SERVER.itemHeatingModifier.get().floatValue();
        instance.setTemperature(temp);
    }

}
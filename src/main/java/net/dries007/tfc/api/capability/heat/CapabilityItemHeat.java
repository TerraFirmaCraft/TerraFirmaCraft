/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.heat;

import net.minecraft.item.ItemStack;
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
    public static final float MAX_TEMPERATURE = 1600f;

    // todo: make this configurable
    // todo: adjust this to change how fast things (Fire Pit / Charcoal Forge) heat up items (item_heating_mod) or how fast it heats up (temperature_modifier)
    public static final float TEMPERATURE_MODIFIER = 1f;
    public static final float ITEM_HEATING_MODIFIER = 3f;

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IItemHeat.class, new DumbStorage<>(), ItemHeatHandler::new);
    }

    /**
     * Call this from within IItemHeat#getTemperature();
     */
    public static float adjustTemp(float temp, float heatCapacity, long ticksSinceUpdate)
    {
        if (ticksSinceUpdate == -1) return 0;
        final float newTemp = temp - heatCapacity * (float) ticksSinceUpdate * (float) ConfigTFC.GENERAL.temperatureModifier;
        return newTemp < MIN_TEMPERATURE ? MIN_TEMPERATURE : newTemp;
    }

    /**
     * Use this to increase the heat on an item stack
     */
    public static void addTemp(ItemStack stack, float modifier)
    {
        final IItemHeat cap = stack.getCapability(ITEM_HEAT_CAPABILITY, null);
        if (cap != null)
        {
            addTemp(cap, modifier);
        }
    }

    /**
     * Use this to increase the heat on an IItemHeat instance.
     * Note: to save the change you will need to still call stack.setTagCompound(cap.serializeNBT());
     *
     * @param modifier the modifier for how much this will heat up: 0 - 1 slows down cooling, 1 = no heating or cooling, > 1 heats, 2 heats at the same rate it cools
     */
    public static void addTemp(IItemHeat instance, float modifier)
    {
        final float temp = instance.getTemperature() + modifier * instance.getHeatCapacity() * (float) ConfigTFC.GENERAL.temperatureModifier;
        instance.setTemperature(temp > MAX_TEMPERATURE ? MAX_TEMPERATURE : temp);
    }
}

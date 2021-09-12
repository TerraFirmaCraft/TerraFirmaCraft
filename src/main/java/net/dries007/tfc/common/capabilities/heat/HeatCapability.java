/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class HeatCapability
{
    @CapabilityInject(IHeat.class)
    public static final Capability<IHeat> CAPABILITY = Helpers.notNull();
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "item_heat");
    public static final IndirectHashCollection<Item, HeatDefinition> CACHE = new IndirectHashCollection<>(HeatDefinition::getValidItems);
    public static final DataManager<HeatDefinition> MANAGER = new DataManager.Instance<>(HeatDefinition::new, "item_heats", "item heat");

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

    public static float adjustDeviceTemp(float temp, float baseTarget, int airTicks, boolean isRaining)
    {
        float target = targetDeviceTemp(baseTarget, airTicks, isRaining);
        if (temp != target)
        {
            float delta = TFCConfig.SERVER.heatingModifier.get().floatValue();
            float deltaPositive = 1, deltaNegative = 1;
            if (airTicks > 0)
            {
                deltaPositive = 2f;
                deltaNegative = 0.5f;
            }
            return adjustTempTowards(temp, target, delta * deltaPositive, delta * deltaNegative);
        }
        return target;
    }

    public static float targetDeviceTemp(float target, int airTicks, boolean isRaining)
    {
        if (airTicks > 0)
        {
            float airInfluence = 4f * airTicks;
            if (airInfluence > 600f)
            {
                airInfluence = 600f;
            }
            target += Math.min(airInfluence, target * 0.5f);
        }
        if (isRaining)
        {
            target -= 300;
            if (target < 0)
            {
                target = 0;
            }
        }
        return target;
    }

    /**
     * Call this from within {@link IHeat#getTemperature()}
     */
    public static float adjustTemp(float temp, float heatCapacity, long ticksSinceUpdate)
    {
        if (ticksSinceUpdate <= 0) return temp;
        final float newTemp = temp - heatCapacity * (float) (ticksSinceUpdate * TFCConfig.SERVER.heatingModifier.get());
        return newTemp < 0 ? 0 : newTemp;
    }

    public static void addTemp(IHeat instance, float target)
    {
        // Default modifier = 3 (2x normal cooling)
        addTemp(instance, target, 3);
    }

    /**
     * Use this to increase the heat on an IItemHeat instance.
     *
     * @param modifier the modifier for how much this will heat up: 0 - 1 slows down cooling, 1 = no heating or cooling, > 1 heats, 2 heats at the same rate of normal cooling, 2+ heats faster
     */
    public static void addTemp(IHeat instance, float target, float modifier)
    {
        float temp = instance.getTemperature() + modifier * instance.getHeatCapacity() * TFCConfig.SERVER.heatingModifier.get().floatValue();
        if (temp > target)
        {
            temp = target;
        }
        instance.setTemperature(temp);
    }

    @Nullable
    public static HeatDefinition get(ItemStack stack)
    {
        for (HeatDefinition def : CACHE.getAll(stack.getItem()))
        {
            if (def.matches(stack))
            {
                return def;
            }
        }
        return null;
    }
}
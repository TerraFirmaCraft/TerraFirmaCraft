/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.collections.IndirectHashCollection;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class HeatCapability
{
    // For heat defined on item stacks
    public static final Capability<IHeat> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "item_heat");

    // For heat providers and consumers defined on blocks
    public static final Capability<IHeatBlock> BLOCK_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation BLOCK_KEY = new ResourceLocation(MOD_ID, "block_heat");

    public static final DataManager<HeatDefinition> MANAGER = new DataManager<>("item_heats", "item heat", HeatDefinition::new, HeatDefinition::new, HeatDefinition::encode, DataManagerSyncPacket.THeatDefinition::new);
    public static final IndirectHashCollection<Item, HeatDefinition> CACHE = IndirectHashCollection.create(HeatDefinition::getValidItems, MANAGER::getValues);

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

    public static float adjustTempTowards(float temp, float target)
    {
        return adjustTempTowards(temp, target, 1, 1);
    }

    public static float adjustTempTowards(float temp, float target, float delta)
    {
        return adjustTempTowards(temp, target, delta, delta);
    }

    public static float adjustTempTowards(float temp, float target, float deltaPositive, float deltaNegative)
    {
        final float delta = TFCConfig.SERVER.heatingModifier.get().floatValue();
        if (temp < target)
        {
            return Math.min(temp + delta * deltaPositive, target);
        }
        else if (temp > target)
        {
            return Math.max(temp - delta * deltaNegative, target);
        }
        else
        {
            return target;
        }
    }

    /**
     * Adjusts the temperature of a device, after one tick, using some common factors
     *
     * @param temp       The current temperature of the device.
     * @param baseTarget The baseline "target temperature" of the device. This is the temperature of whatever is heating (fuel, or external sources)
     * @param airTicks   Air ticks, provided by a bellows or other similar device. Can raise the target temperature by up to 600 C
     * @param isRaining  If it is raining. Will lower the target temperature by 300 C
     * @return The temperature after one tick's worth of movement.
     */
    public static float adjustDeviceTemp(float temp, float baseTarget, int airTicks, boolean isRaining)
    {
        float target = targetDeviceTemp(baseTarget, airTicks, isRaining);
        if (temp != target)
        {
            float deltaPositive = 1, deltaNegative = 1;
            if (airTicks > 0)
            {
                deltaPositive = 2f;
                deltaNegative = 0.5f;
            }
            return adjustTempTowards(temp, target, deltaPositive, deltaNegative);
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
        if (ticksSinceUpdate <= 0)
        {
            return temp;
        }
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

    /**
     * Common logic for block entities to consume fuel during larger time skips.
     *
     * @param ticks           Ticks since the last calendar update. This is decremented as the method checks different fuel consumption options.
     * @param inventory       Inventory to be modified (this should contain the fuel)
     * @param burnTicks       Remaining burn ticks of the fuel being burned
     * @param burnTemperature Current burning temperature of the TE (this is the fuel's target temperature)
     * @param slotStart       Index of the first fuel slot
     * @param slotEnd         Index of the last fuel slot
     * @return The remainder after consuming fuel, along with an amount (possibly > 0) of ticks that haven't been accounted for.
     */
    public static Remainder consumeFuelForTicks(long ticks, IItemHandlerModifiable inventory, int burnTicks, float burnTemperature, int slotStart, int slotEnd)
    {
        if (burnTicks > ticks)
        {
            burnTicks -= ticks;
            return new Remainder(burnTicks, burnTemperature, 0L);
        }
        else
        {
            ticks -= burnTicks;
            burnTicks = 0;
        }
        // Need to consume fuel
        for (int i = slotStart; i <= slotEnd; i++)
        {
            ItemStack fuelStack = inventory.getStackInSlot(i);
            Fuel fuel = Fuel.get(fuelStack);
            if (fuel != null)
            {
                inventory.setStackInSlot(i, ItemStack.EMPTY);
                if (fuel.getDuration() > ticks)
                {
                    burnTicks = (int) (fuel.getDuration() - ticks);
                    burnTemperature = fuel.getTemperature();
                    return new Remainder(burnTicks, burnTemperature, 0L);
                }
                else
                {
                    ticks -= fuel.getDuration();
                    burnTicks = 0;
                }
            }
        }
        return new Remainder(burnTicks, burnTemperature, ticks);
    }

    public record Remainder(int burnTicks, float burnTemperature, long ticks) {}
}
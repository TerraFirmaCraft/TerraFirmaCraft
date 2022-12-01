/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import java.util.Iterator;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class HeatCapability
{
    // For heat defined on item stacks
    public static final Capability<IHeat> CAPABILITY = Helpers.capability(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "item_heat");

    // For heat providers and consumers defined on blocks
    public static final Capability<IHeatBlock> BLOCK_CAPABILITY = Helpers.capability(new CapabilityToken<>() {});
    public static final ResourceLocation BLOCK_KEY = new ResourceLocation(MOD_ID, "block_heat");

    public static final DataManager<HeatDefinition> MANAGER = new DataManager<>(Helpers.identifier("item_heats"), "item heat", HeatDefinition::new, HeatDefinition::new, HeatDefinition::encode, Packet::new);
    public static final IndirectHashCollection<Item, HeatDefinition> CACHE = IndirectHashCollection.create(HeatDefinition::getValidItems, MANAGER::getValues);

    public static final float POTTERY_HEAT_CAPACITY = 1.2f;

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
        final float delta = TFCConfig.SERVER.deviceHeatingModifier.get().floatValue();
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
     * Adjusts a temperature and timestamp combination for passive heat decay.
     *
     * @param temperature The last known temperature
     * @param heatCapacity The heat capacity, in Energy / °C
     * @param ticksSinceUpdate The number of ticks since the last known temperature
     */
    public static float adjustTemp(float temperature, float heatCapacity, long ticksSinceUpdate)
    {
        if (ticksSinceUpdate <= 0)
        {
            return temperature;
        }
        final float newTemperature = temperature - (float) (ticksSinceUpdate * TFCConfig.SERVER.itemCoolingModifier.get()) / heatCapacity;
        return newTemperature < 0 ? 0 : newTemperature;
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
        float temp = instance.getTemperature() + (TFCConfig.SERVER.itemCoolingModifier.get().floatValue() - 1 + modifier * TFCConfig.SERVER.itemHeatingModifier.get().floatValue()) / instance.getHeatCapacity();
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
        return consumeFuelForTicks(ticks, burnTicks, burnTemperature, Helpers.iterate(inventory, slotStart, 1 + slotEnd));
    }

    /**
     * Common logic for block entities to consume fuel during larger time skips.
     *
     * @param ticks           Ticks since the last calendar update. This is decremented as the method checks different fuel consumption options.
     * @param burnTicks       Remaining burn ticks of the fuel being burned
     * @param burnTemperature Current burning temperature of the TE (this is the fuel's target temperature)
     * @param fuelStacks      An iterator of fuel stacks which supports removal (to indicate fuel is consumed).
     * @return The remainder after consuming fuel, along with an amount (possibly > 0) of ticks that haven't been accounted for.
     */
    public static Remainder consumeFuelForTicks(long ticks, int burnTicks, float burnTemperature, Iterable<ItemStack> fuelStacks)
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

        final Iterator<ItemStack> iterator = fuelStacks.iterator();
        while (iterator.hasNext())
        {
            final ItemStack fuelStack = iterator.next();
            final Fuel fuel = Fuel.get(fuelStack);
            if (fuel != null)
            {
                iterator.remove(); // Consume fuel item stack
                if (fuel.getDuration() > ticks)
                {
                    burnTicks = (int) (fuel.getDuration() - ticks);
                    burnTemperature = fuel.getTemperature();
                    return new Remainder(burnTicks, burnTemperature, 0L);
                }
                else
                {
                    ticks -= fuel.getDuration();
                }
            }
        }
        return new Remainder(burnTicks, burnTemperature, ticks);
    }

    public static void provideHeatTo(Level level, BlockPos pos, float temperature)
    {
        final BlockEntity entity = level.getBlockEntity(pos);
        if (entity != null)
        {
            entity.getCapability(HeatCapability.BLOCK_CAPABILITY).ifPresent(cap -> cap.setTemperatureIfWarmer(temperature));
        }
    }

    public record Remainder(int burnTicks, float burnTemperature, long ticks) {}

    public static class Packet extends DataManagerSyncPacket<HeatDefinition> {}
}
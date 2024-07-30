/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.heat;

import java.util.Iterator;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.BlockCapabilities;
import net.dries007.tfc.common.capabilities.ItemCapabilities;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.data.Fuel;

public final class HeatCapability
{
    public static final DataManager<HeatDefinition> MANAGER = new DataManager<>(Helpers.identifier("item_heat"), HeatDefinition.CODEC, HeatDefinition.STREAM_CODEC);
    public static final IndirectHashCollection<Item, HeatDefinition> CACHE = IndirectHashCollection.create(r -> RecipeHelpers.itemKeys(r.ingredient()), MANAGER::getValues);

    public static final float POTTERY_HEAT_CAPACITY = 1.2f;

    /**
     * Returns the heat implementation for the given stack. This is (1) mutable, and (2) uses the correct implementation,
     * including fallbacks to simple {@link HeatView} implementations. Use whenever querying the capability of an external
     * stack.
     */
    @Nullable
    public static IHeat get(ItemStack stack)
    {
        // First, query a heat capability, in case a custom implementation is desired
        final @Nullable IHeat capability = stack.getCapability(ItemCapabilities.HEAT);
        if (capability != null)
        {
            return capability;
        }
        // Then, fallback to a heat component, and use the default implementation (HeatView)
        final @Nullable HeatComponent value = stack.get(TFCComponents.HEAT);
        return value != null ? new HeatView(stack, value) : null;
    }

    /**
     * Returns an immutable, view-only implementation for a given stack. This uses the correct implementation, but avoids
     * some overhead of {@link #get(ItemStack)}, and avoids exposing accidental mutability, if that is not desired.
     */
    @Nullable
    public static IHeatView view(ItemStack stack)
    {
        // First, query a heat capability, in case a custom implementation is desired
        // Otherwise, fallback to the view provided from the component
        final @Nullable IHeat capability = stack.getCapability(ItemCapabilities.HEAT);
        return capability != null ? capability : stack.get(TFCComponents.HEAT);
    }

    /**
     * Returns a mutable view of the <strong>raw, internal, component</strong> of a heat. This is not using the external implementation,
     * and should ONLY be used in situations where direct, mutable access to the internal component is desired. For example, in the
     * constructor of a capability implementation, we desire the component, and have reasonable assurances that it must exist.
     * @throws NullPointerException if the capability does not exist
     */
    public static IHeat mutableView(ItemStack stack)
    {
        return new HeatView(stack, Objects.requireNonNull(stack.get(TFCComponents.HEAT), () -> "Expected a heat component to be present on" + stack));
    }

    public static boolean has(ItemStack stack)
    {
        // N.B. Any item providing a heat capability must also provide a heat component, so this check is sufficient
        return stack.has(TFCComponents.HEAT);
    }

    /**
     * @return The temperature of a given {@code stack}, or {@code 0} if the item is not heatable.
     */
    public static float getTemperature(ItemStack stack)
    {
        final @Nullable IHeatView heat = view(stack);
        return heat != null ? heat.getTemperature() : 0;
    }

    /**
     * Sets the temperature of a given {@code stack} to {@code temperature}, if the stack has a heat capability.
     */
    public static void setTemperature(ItemStack stack, float temperature)
    {
        final @Nullable IHeat heat = get(stack);
        if (heat != null) heat.setTemperature(temperature);
    }

    /**
     * @return {@code true} if {@code stack} has a heat capability, and is currently hot, i.e. nonzero temperature.
     */
    public static boolean isHot(ItemStack stack)
    {
        return getTemperature(stack) > 0;
    }

    @Nullable
    public static HeatDefinition getDefinition(ItemStack stack)
    {
        return RecipeHelpers.getRecipe(CACHE, stack, stack.getItem());
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
    public static void addTemp(IHeat instance, float targetTemperature, float modifier)
    {
        modifier = TFCConfig.SERVER.itemCoolingModifier.get().floatValue() - 1 + modifier * TFCConfig.SERVER.itemHeatingModifier.get().floatValue();

        final float initialTemperature = instance.getTemperature();
        float newTemperature = initialTemperature + modifier / instance.getHeatCapacity();
        if (newTemperature > targetTemperature)
        {
            newTemperature = targetTemperature;
        }
        if (newTemperature > initialTemperature)
        {
            instance.setTemperature(newTemperature);
        }
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
                if (fuel.duration() > ticks)
                {
                    burnTicks = (int) (fuel.duration() - ticks);
                    burnTemperature = fuel.temperature();
                    return new Remainder(burnTicks, burnTemperature, 0L);
                }
                else
                {
                    ticks -= fuel.duration();
                }
            }
        }
        return new Remainder(burnTicks, burnTemperature, ticks);
    }

    public static void provideHeatTo(Level level, BlockPos pos, Direction to, float temperature)
    {
        final @Nullable IHeatConsumer heat = level.getCapability(BlockCapabilities.HEAT, pos, to);
        if (heat != null)
        {
            heat.setTemperature(temperature);
        }
    }

    public record Remainder(int burnTicks, float burnTemperature, long ticks) {}
}
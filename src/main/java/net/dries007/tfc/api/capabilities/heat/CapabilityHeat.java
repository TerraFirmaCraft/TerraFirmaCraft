/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capabilities.heat;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.crafting.CraftingHelper;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.NoopStorage;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class CapabilityHeat
{
    @CapabilityInject(IHeat.class)
    public static final Capability<IHeat> CAPABILITY = Helpers.notNull();
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "item_heat");

    public static void setup()
    {
        CapabilityManager.INSTANCE.register(IHeat.class, new NoopStorage<>(), HeatHandler::new);
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
        final float newTemp = temp - heatCapacity * (float) ticksSinceUpdate * 1; // todo config -> (float) ConfigTFC.Devices.TEMPERATURE.globalModifier;
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
        final float temp = instance.getTemperature() + modifier * instance.getHeatCapacity() * 1; // todo config -> (float) ConfigTFC.Devices.TEMPERATURE.globalModifier;
        instance.setTemperature(temp);
    }

    /**
     * Datapack manager for loading heat capability
     */
    public static class HeatManager extends DataManager<HeatWrapper>
    {
        public static final IndirectHashCollection<Item, HeatWrapper> CACHE = new IndirectHashCollection<>(HeatWrapper::getValidItems);
        public static final HeatManager INSTANCE = new HeatManager();

        private HeatManager()
        {
            super(new GsonBuilder().create(), "item_heats", "item heat");
        }

        @Override
        protected HeatWrapper read(ResourceLocation id, JsonObject obj)
        {
            return new HeatWrapper(id, obj);
        }
    }

    public static class HeatWrapper
    {
        private final ResourceLocation id;
        private final Supplier<IHeat> capability;
        private final Ingredient ingredient;

        public HeatWrapper(ResourceLocation id, JsonObject obj)
        {
            this.id = id;
            float heatCapacity = JSONUtils.getFloat(obj, "heat_capacity");
            float forgingTemp = JSONUtils.getFloat(obj, "forging_temperature", 0);
            float weldingTemp = JSONUtils.getFloat(obj, "welding_temperature", 0);
            this.ingredient = CraftingHelper.getIngredient(JSONUtils.getJsonObject(obj, "ingredient"));
            this.capability = () -> new HeatHandler(heatCapacity, forgingTemp, weldingTemp);
        }

        public ResourceLocation getId()
        {
            return id;
        }

        public IHeat getCapability()
        {
            return capability.get();
        }

        public boolean isValid(ItemStack stack)
        {
            return ingredient.test(stack);
        }

        public Collection<Item> getValidItems()
        {
            return Arrays.stream(this.ingredient.getMatchingStacks()).map(ItemStack::getItem).collect(Collectors.toSet());
        }
    }
}

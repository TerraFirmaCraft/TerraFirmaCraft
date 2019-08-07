/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.crafttweaker;

import net.minecraft.item.ItemStack;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.dries007.tfc.api.capability.damage.CapabilityDamageResistance;
import net.dries007.tfc.api.capability.damage.DamageResistance;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodHandler;
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.ForgeableHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.ItemSizeHandler;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.fuel.Fuel;
import net.dries007.tfc.util.fuel.FuelManager;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.ItemRegistry")
@ZenRegister
public class CTItemRegistry
{
    @ZenMethod
    public static void registerItemSize(crafttweaker.api.item.IIngredient input, String inputSize, String inputWeight)
    {
        if (input == null) throw new IllegalArgumentException("Input not allowed to be empty!");
        if (input instanceof ILiquidStack)
            throw new IllegalArgumentException("There is a fluid where it's supposed to be an item!");
        IIngredient inputIngredient = CTHelper.getInternalIngredient(input);
        Size size = Size.valueOf(inputSize.toUpperCase());
        Weight weight = Weight.valueOf(inputWeight.toUpperCase());
        if (CapabilityItemSize.CUSTOM_ITEMS.get(inputIngredient) != null)
        {
            throw new IllegalStateException("Input registered more than once!");
        }
        else
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @SuppressWarnings("unchecked")
                @Override
                public void apply()
                {
                    CapabilityItemSize.CUSTOM_ITEMS.put(inputIngredient, () -> new ItemSizeHandler(size, weight, true));
                }

                @Override
                public String describe()
                {
                    return "Registered size and weight for " + input.toCommandString();
                }
            });
        }
    }

    @ZenMethod
    public static void registerItemHeat(crafttweaker.api.item.IIngredient input, float heatCapacity, float meltTemp, boolean forgeable)
    {
        if (input == null) throw new IllegalArgumentException("Input not allowed to be empty!");
        if (input instanceof ILiquidStack)
            throw new IllegalArgumentException("There is a fluid where it's supposed to be an item!");
        if (heatCapacity <= 0 || meltTemp <= 0)
            throw new IllegalArgumentException("Heat capacity and melt temp must be higher than 0!");
        IIngredient inputIngredient = CTHelper.getInternalIngredient(input);
        if (CapabilityItemHeat.CUSTOM_ITEMS.get(inputIngredient) != null || CapabilityForgeable.CUSTOM_ITEMS.get(inputIngredient) != null)
        {
            throw new IllegalStateException("Input already registered in forge/heat capability!");
        }
        else
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @SuppressWarnings("unchecked")
                @Override
                public void apply()
                {
                    if (forgeable)
                    {
                        CapabilityForgeable.CUSTOM_ITEMS.put(inputIngredient, () -> new ForgeableHandler(null, heatCapacity, meltTemp));
                    }
                    else
                    {
                        CapabilityItemHeat.CUSTOM_ITEMS.put(inputIngredient, () -> new ItemHeatHandler(null, heatCapacity, meltTemp));
                    }
                }

                @Override
                public String describe()
                {
                    return "Registered heat capacity for " + input.toCommandString();
                }
            });
        }
    }

    @ZenMethod
    public static void registerFood(crafttweaker.api.item.IIngredient input, float[] nutrients, float calories, float water, float decay)
    {
        if (input == null) throw new IllegalArgumentException("Input not allowed to be empty!");
        if (input instanceof ILiquidStack)
            throw new IllegalArgumentException("There is a fluid where it's supposed to be an item!");
        IIngredient inputIngredient = CTHelper.getInternalIngredient(input);
        if (nutrients.length != 5) throw new IllegalArgumentException("There are 5 nutrients that must be specified!");
        if (CapabilityFood.CUSTOM_FOODS.get(inputIngredient) != null)
        {
            throw new IllegalStateException("Food registered more than once!");
        }
        else
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @SuppressWarnings("unchecked")
                @Override
                public void apply()
                {
                    CapabilityFood.CUSTOM_FOODS.put(inputIngredient, () -> new FoodHandler(null, nutrients, calories, water, decay));
                }

                @Override
                public String describe()
                {
                    return "Registered food stats for " + input.toCommandString();
                }
            });
        }
    }

    @ZenMethod
    public static void registerArmor(crafttweaker.api.item.IIngredient input, float crushingModifier, float piercingModifier, float slashingModifier)
    {
        if (input == null) throw new IllegalArgumentException("Input not allowed to be empty!");
        if (input instanceof ILiquidStack)
            throw new IllegalArgumentException("There is a fluid where it's supposed to be an item!");
        IIngredient inputIngredient = CTHelper.getInternalIngredient(input);
        if (CapabilityDamageResistance.CUSTOM_ARMOR.get(inputIngredient) != null)
        {
            throw new IllegalStateException("Armor registered more than once!");
        }
        else
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @SuppressWarnings("unchecked")
                @Override
                public void apply()
                {
                    CapabilityDamageResistance.CUSTOM_ARMOR.put(inputIngredient, () -> new DamageResistance(crushingModifier, piercingModifier, slashingModifier));
                }

                @Override
                public String describe()
                {
                    return "Registered armor stats for " + input.toCommandString();
                }
            });
        }
    }

    @ZenMethod
    public static void registerFuel(IItemStack itemStack, int burnTicks, float temperature, boolean forgeFuel)
    {
        if (itemStack == null) throw new IllegalArgumentException("Item not allowed to be empty!");
        if (burnTicks <= 0 || temperature <= 0)
            throw new IllegalArgumentException("Temp and burn ticks must be higher than 0!");
        ItemStack stack = ((ItemStack) itemStack.getInternal());
        if (FuelManager.isItemFuel(stack))
        {
            throw new IllegalStateException("Fuel stack registered more than once!");
        }
        else
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    Fuel fuel = new Fuel(stack, burnTicks, temperature, forgeFuel);
                    FuelManager.addFuel(fuel);
                }

                @Override
                public String describe()
                {
                    return "Registered fuel stats for " + stack.getDisplayName();
                }
            });
        }
    }
}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.crafttweaker;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodHandler;
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.ForgeableHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.ItemSizeHandler;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.recipes.heat.HeatRecipe;
import net.dries007.tfc.objects.recipes.heat.HeatRecipeManager;
import net.dries007.tfc.util.fuel.Fuel;
import net.dries007.tfc.util.fuel.FuelManager;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.ItemRegistry")
@ZenRegister
public class ItemRegistry
{
    @ZenMethod
    public static void registerItemSize(IItemStack itemStack, String inputSize, String inputWeight)
    {
        if (itemStack == null) throw new IllegalArgumentException("Item not allowed to be empty!");
        ItemStack stack = ((ItemStack) itemStack.getInternal());
        Size size = Size.valueOf(inputSize.toUpperCase());
        Weight weight = Weight.valueOf(inputWeight.toUpperCase());
        if (CapabilityItemSize.CUSTOM_ITEMS.get(stack.getItem()) != null)
        {
            throw new IllegalStateException("Item registered more than once!");
        }
        else
        {
            CraftTweakerAPI.apply(new RegisterSize(stack, size, weight));
        }
    }

    @ZenMethod
    public static void registerItemHeat(IItemStack itemStack, float heatCapacity, float meltTemp, boolean forgeable)
    {
        if (itemStack == null) throw new IllegalArgumentException("Item not allowed to be empty!");
        if (heatCapacity <= 0 || meltTemp <= 0)
            throw new IllegalArgumentException("Heat capacity and melt temp must be higher than 0!");
        ItemStack stack = ((ItemStack) itemStack.getInternal());
        if (CapabilityItemHeat.CUSTOM_ITEMS.get(stack.getItem()) != null || CapabilityForgeable.CUSTOM_ITEMS.get(stack.getItem()) != null)
        {
            throw new IllegalStateException("Item already registered in forge/heat capability!");
        }
        else
        {
            CraftTweakerAPI.apply(new RegisterHeat(stack, heatCapacity, meltTemp, forgeable));
        }
    }

    @ZenMethod
    public static void registerHeatRecipe(IItemStack input, IItemStack output)
    {
        if (input == null || output == null)
            throw new IllegalArgumentException("Input and output are not allowed to be empty!");
        ItemStack istack = ((ItemStack) input.getInternal());
        ItemStack ostack = ((ItemStack) output.getInternal());
        IItemHeat icap = istack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        IItemHeat ocap = ostack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        if (icap == null || ocap == null) throw new IllegalStateException("Input and output must have heat registry!");
        if (HeatRecipeManager.get(istack) != null)
        {
            throw new IllegalStateException("There is a recipe registered already for " + istack.getDisplayName() + "!");
        }
        else
        {
            CraftTweakerAPI.apply(new RegisterHeatRecipe(istack, ostack));
        }
    }

    @ZenMethod
    public static void registerFood(IItemStack itemStack, float[] nutrients, float calories, float water, float decay)
    {
        if (itemStack == null) throw new IllegalArgumentException("Item not allowed to be empty!");
        ItemStack stack = ((ItemStack) itemStack.getInternal());
        if (!(stack.getItem() instanceof ItemFood)) throw new IllegalArgumentException("Item is not Food!");
        if (nutrients.length != 5) throw new IllegalArgumentException("There are 5 nutrients that must be specified!");
        if (CapabilityFood.CUSTOM_FOODS.get(stack.getItem()) != null)
        {
            throw new IllegalStateException("Food registered more than once!");
        }
        else
        {
            CraftTweakerAPI.apply(new RegisterFood(stack, nutrients, calories, water, decay));
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
            CraftTweakerAPI.apply(new RegisterFuel(stack, burnTicks, temperature, forgeFuel));
        }
    }

    //TODO add register for damage types?
    //TODO add register for armor resistances?

    private static class RegisterSize implements IAction
    {
        private final ItemStack stack;
        private final Size size;
        private final Weight weight;

        RegisterSize(ItemStack stack, Size size, Weight weight)
        {
            this.stack = stack;
            this.size = size;
            this.weight = weight;
        }

        @Override
        public void apply()
        {
            boolean canStack = stack.getMaxStackSize() > 1;
            ItemSizeHandler handler = new ItemSizeHandler(size, weight, canStack);
            CapabilityItemSize.CUSTOM_ITEMS.put(stack.getItem(), handler);
        }

        @Override
        public String describe()
        {
            return "Registered size and weight for " + stack.getDisplayName();
        }
    }

    private static class RegisterHeat implements IAction
    {
        private final ItemStack stack;
        private final float heatCapacity;
        private final float meltTemp;
        private final boolean forgeable;

        RegisterHeat(ItemStack stack, float heatCapacity, float meltTemp, boolean forgeable)
        {
            this.stack = stack;
            this.heatCapacity = heatCapacity;
            this.meltTemp = meltTemp;
            this.forgeable = forgeable;
        }

        @Override
        public void apply()
        {
            if (forgeable)
            {
                ForgeableHandler handler = new ForgeableHandler(null, heatCapacity, meltTemp);
                CapabilityForgeable.CUSTOM_ITEMS.put(stack.getItem(), handler);
            }
            else
            {
                ItemHeatHandler handler = new ItemHeatHandler(null, heatCapacity, meltTemp);
                CapabilityItemHeat.CUSTOM_ITEMS.put(stack.getItem(), handler);
            }
        }

        @Override
        public String describe()
        {
            return "Registered heat capacity for " + stack.getDisplayName();
        }
    }

    private static class RegisterHeatRecipe implements IAction
    {
        private final ItemStack input;
        private final ItemStack output;

        RegisterHeatRecipe(ItemStack input, ItemStack output)
        {
            this.input = input;
            this.output = output;
        }

        @Override
        public void apply()
        {
            HeatRecipe recipe = new HeatRecipe(output, input);
            HeatRecipeManager.add(recipe);
        }

        @Override
        public String describe()
        {
            return "Registered heat recipe for " + input.getDisplayName() + " -> " + output.getDisplayName();
        }
    }

    private static class RegisterFood implements IAction
    {
        private final ItemStack stack;
        private final float[] nutrients;
        private final float calories, water, decay;

        RegisterFood(ItemStack stack, float[] nutrients, float calories, float water, float decay)
        {
            this.stack = stack;
            this.nutrients = nutrients;
            this.calories = calories;
            this.water = water;
            this.decay = decay;
        }

        @Override
        public void apply()
        {
            FoodHandler handler = new FoodHandler(stack.getTagCompound(), nutrients, calories, water, decay);
            CapabilityFood.CUSTOM_FOODS.put((ItemFood) stack.getItem(), handler);
        }

        @Override
        public String describe()
        {
            return "Registered food stats for " + stack.getDisplayName();
        }
    }

    private static class RegisterFuel implements IAction
    {
        private final Fuel fuel;
        private final String stackName;

        RegisterFuel(ItemStack stack, int burnTicks, float temperature, boolean forgeFuel)
        {
            stackName = stack.getDisplayName();
            fuel = new Fuel(stack, burnTicks, temperature, forgeFuel);
        }

        @Override
        public void apply()
        {
            FuelManager.addFuel(fuel);
        }

        @Override
        public String describe()
        {
            return "Registered fuel stats for " + stackName;
        }
    }
}

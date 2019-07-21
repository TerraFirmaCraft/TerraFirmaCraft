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
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.ItemSizeHandler;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
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
    public static void registerFood(IItemStack itemStack, float[] nutrients, float calories, float water, float decay)
    {
        if (itemStack == null) throw new IllegalArgumentException("Item not allowed to be empty!");
        ItemStack stack = ((ItemStack) itemStack.getInternal());
        if (!(stack.getItem() instanceof ItemFood)) throw new IllegalArgumentException("Item is not Food!");
        if (nutrients.length != 5) throw new IllegalArgumentException("There are 5 nutrients that must be specified!");
        if (CapabilityItemSize.CUSTOM_ITEMS.get(stack.getItem()) != null)
        {
            throw new IllegalStateException("Food registered more than once!");
        }
        else
        {
            CraftTweakerAPI.apply(new RegisterFood(stack, nutrients, calories, water, decay));
        }
    }

    //TODO add register for damage types?
    //TODO add register for armor resistances?
    //TODO add register for heat?

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

}

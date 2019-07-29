/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.food;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.api.capability.food.FoodHandler;
import net.dries007.tfc.api.capability.food.FoodHeatHandler;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.agriculture.Food;

public class ItemFoodTFC extends ItemFood
{
    private static final Map<Food, ItemFoodTFC> MAP = new HashMap<>();

    public static ItemFoodTFC get(Food food)
    {
        return MAP.get(food);
    }

    public static ItemStack get(Food food, int amount)
    {
        return new ItemStack(MAP.get(food), amount);
    }

    private final Food food;

    public ItemFoodTFC(@Nonnull Food food)
    {
        super(IFoodStatsTFC.FOOD_HUNGER_AMOUNT, food.getCalories(), food.getCategory() == Food.Category.MEAT);
        this.food = food;
        if (MAP.put(food, this) != null)
        {
            throw new IllegalStateException("There can only be one.");
        }

        // Use "category" here as to not conflict with actual items, i.e. grain
        OreDictionaryHelper.register(this, "category", food.getCategory());
        if (food.getOreDictNames() != null)
        {
            for (Object name : food.getOreDictNames())
            {
                OreDictionaryHelper.register(this, name);
            }
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return food.isHeatable() ? new FoodHeatHandler(nbt, food) : new FoodHandler(nbt, food);
    }
}
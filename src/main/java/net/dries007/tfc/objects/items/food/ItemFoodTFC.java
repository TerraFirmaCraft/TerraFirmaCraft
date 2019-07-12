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

        // todo: make this better and work with all foods somehow
        OreDictionaryHelper.register(this, food.getCategory());
        switch (food)
        {
            case BARLEY_FLOUR:
                OreDictionaryHelper.register(this, "flour_barley");
            case GREEN_APPLE:
            case RED_APPLE:
                OreDictionaryHelper.register(this, "apple");
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FoodHandler(nbt, food);
    }
}
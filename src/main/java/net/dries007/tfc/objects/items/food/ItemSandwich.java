/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.food;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.FoodHandler;
import net.dries007.tfc.api.capability.food.Nutrient;
import net.dries007.tfc.util.agriculture.Food;

@ParametersAreNonnullByDefault
public class ItemSandwich extends ItemFoodTFC
{
    public ItemSandwich(@Nonnull Food food)
    {
        super(food);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new SandwichHandler(nbt, food.getData());
    }

    public static class SandwichHandler extends FoodHandler
    {
        private final FoodData rootData;

        public SandwichHandler(@Nullable NBTTagCompound nbt, FoodData data)
        {
            super(nbt, data);

            this.rootData = data;
        }

        public void initCreationFoods(FoodData bread1, FoodData bread2, List<FoodData> ingredients)
        {
            // Nutrition and saturation of sandwich is (average of breads) + (sum of ingredients)
            float[] nutrition = new float[Nutrient.TOTAL];
            float water = 0, saturation = 0;
            for (int i = 0; i < nutrition.length; i++)
            {
                nutrition[i] = 0.5f * (bread1.getNutrients()[i] + bread2.getNutrients()[i]);
                saturation = 0.5f * (bread1.getSaturation() + bread2.getSaturation());
                water = 0.5f * (bread1.getWater() + bread2.getWater());
            }
            for (FoodData ingredient : ingredients)
            {
                for (int i = 0; i < nutrition.length; i++)
                {
                    nutrition[i] += ingredient.getNutrients()[i];
                    saturation += ingredient.getSaturation();
                    water += ingredient.getWater();
                }
            }
            this.data = new FoodData(4, water, saturation, nutrition, rootData.getDecayModifier());
        }

        @Override
        protected boolean isDynamic()
        {
            return true;
        }
    }
}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.nuturient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.util.agriculture.Food;
import net.dries007.tfc.util.agriculture.Nutrient;

public class NutrientsHandler implements INutrients, ICapabilityProvider
{
    private final float[] nutrients;

    public NutrientsHandler(Food food)
    {
        this(food.getCarbohydrates(), food.getFat(), food.getProtein(), food.getVitamins(), food.getMinerals());
    }

    public NutrientsHandler(float carbohydrates, float fat, float protein, float vitamins, float minerals)
    {
        this.nutrients = new float[] {carbohydrates, fat, protein, vitamins, minerals};
    }

    @Override
    public float getNutrients(ItemStack stack, Nutrient nutrient)
    {
        return nutrients[nutrient.ordinal()];
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityNutrients.CAPABILITY_NUTRIENTS;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityNutrients.CAPABILITY_NUTRIENTS ? (T) this : null;
    }
}

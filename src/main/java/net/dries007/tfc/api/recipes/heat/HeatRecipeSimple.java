/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.heat;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

@ParametersAreNonnullByDefault
public class HeatRecipeSimple extends HeatRecipe
{

    private final ItemStack output;
    private final float maxTemp;

    public HeatRecipeSimple(IIngredient<ItemStack> ingredient, ItemStack output, float transformTemp)
    {
        this(ingredient, output, transformTemp, CapabilityItemHeat.MAX_TEMPERATURE, Metal.Tier.TIER_0);
    }

    public HeatRecipeSimple(IIngredient<ItemStack> ingredient, ItemStack output, float transformTemp, float maxTemp)
    {
        this(ingredient, output, transformTemp, maxTemp, Metal.Tier.TIER_0);
    }

    public HeatRecipeSimple(IIngredient<ItemStack> ingredient, ItemStack output, float transformTemp, Metal.Tier minTier)
    {
        this(ingredient, output, transformTemp, CapabilityItemHeat.MAX_TEMPERATURE, minTier);
    }

    public HeatRecipeSimple(IIngredient<ItemStack> ingredient, ItemStack output, float transformTemp, float maxTemp, Metal.Tier minTier)
    {
        super(ingredient, transformTemp, minTier);
        this.output = output;
        this.maxTemp = maxTemp;
    }

    @Override
    @Nonnull
    public ItemStack getOutputStack(ItemStack input)
    {
        IItemHeat heat = input.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        if (heat != null && heat.getTemperature() <= maxTemp)
        {
            ItemStack outputStack = output.copy();
            IItemHeat outputHeat = outputStack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            if (outputHeat != null)
            {
                // Copy heat if possible
                outputHeat.setTemperature(heat.getTemperature());
            }
            return outputStack;
        }
        return ItemStack.EMPTY;
    }
}

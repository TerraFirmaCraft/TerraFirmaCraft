/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.anvil;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurable;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.forge.ForgeRule;

/**
 * This is an anvil recipe that will split an {@link IForgeableMeasurable} into a specific "chunk" size
 * Used by blooms to split a 560 -> 5x 100 blooms and 1x 60 bloom for example
 */
@ParametersAreNonnullByDefault
public class AnvilRecipeSplitting extends AnvilRecipeMeasurable
{
    protected int splitAmount;

    public AnvilRecipeSplitting(ResourceLocation name, IIngredient<ItemStack> input, int splitAmount, Metal.Tier minTier, ForgeRule... rules) throws IllegalArgumentException
    {
        super(name, input, ItemStack.EMPTY, minTier, rules);
        this.splitAmount = splitAmount;
    }

    @Override
    public boolean matches(ItemStack input)
    {
        if (!super.matches(input)) return false;
        //Splitable if the output is at least two(don't change this or you will have duplicates)
        IForgeable cap = input.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if (cap instanceof IForgeableMeasurable)
            return splitAmount < ((IForgeableMeasurable) cap).getMetalAmount();
        return false;
    }


    @Override
    @Nonnull
    public NonNullList<ItemStack> getOutput(ItemStack input)
    {
        if (matches(input))
        {
            IForgeable inCap = input.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
            if (inCap instanceof IForgeableMeasurable)
            {
                int metalAmount = ((IForgeableMeasurable) inCap).getMetalAmount();
                int surplus = metalAmount % splitAmount;
                int outCount = metalAmount / splitAmount;

                NonNullList<ItemStack> output = NonNullList.create();
                for (int i = 0; i < outCount; i++)
                {
                    ItemStack dump = input.copy();
                    IForgeable cap = dump.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
                    if (cap instanceof IForgeableMeasurable)
                    {
                        cap.setWork(0); //Reset work without resetting temp
                        cap.setRecipe((ResourceLocation) null);
                        ((IForgeableMeasurable) cap).setMetalAmount(splitAmount);
                    }
                    output.add(dump);
                }
                if (surplus > 0)
                {
                    ItemStack dumpSurplus = input.copy();
                    IForgeable cap = dumpSurplus.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
                    if (cap instanceof IForgeableMeasurable)
                    {
                        cap.setWork(0); //Reset work without resetting temp
                        cap.setRecipe((ResourceLocation) null);
                        ((IForgeableMeasurable) cap).setMetalAmount(surplus);
                    }

                    output.add(dumpSurplus);
                }
                return output;
            }
        }
        return EMPTY;
    }
}

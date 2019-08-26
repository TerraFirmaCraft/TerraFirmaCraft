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

/*
 * Use this AnvilRecipe implementation if your recipe has metal amount to transfer from input to output
 * *Or* if your input must have a specific amount to work
 */
@ParametersAreNonnullByDefault
public class AnvilRecipeMeasurable extends AnvilRecipe
{
    protected int specificAmount;
    protected boolean isCopyRule;

    /*
     * Use this constructor to make a recipe where only accepts input if it has a specific amount of metal.
     */
    public AnvilRecipeMeasurable(ResourceLocation name, IIngredient<ItemStack> input, ItemStack output, int specificAmount, Metal.Tier minTier, ForgeRule... rules) throws IllegalArgumentException
    {
        super(name, input, output, minTier, rules);
        this.isCopyRule = false;
        this.specificAmount = specificAmount;
    }

    /*
     * Use this constructor to build a recipe where the metal amount is copied from input to output.
     */
    public AnvilRecipeMeasurable(ResourceLocation name, IIngredient<ItemStack> input, ItemStack output, Metal.Tier minTier, ForgeRule... rules) throws IllegalArgumentException
    {
        super(name, input, output, minTier, rules);
        this.isCopyRule = true;
    }

    @Override
    public boolean matches(ItemStack input)
    {
        if (!super.matches(input)) return false;
        if (isCopyRule)
        {
            return true;
        }
        else
        {
            IForgeable cap = input.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
            if (cap instanceof IForgeableMeasurable)
                return specificAmount == ((IForgeableMeasurable) cap).getMetalAmount();
            return false;
        }
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getOutput(ItemStack input)
    {
        if (matches(input))
        {
            NonNullList<ItemStack> out = super.getOutput(input);
            if (isCopyRule)
            {
                IForgeable inCap = input.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
                if (inCap instanceof IForgeableMeasurable)
                {
                    IForgeable outCap = out.get(0).getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
                    if (outCap instanceof IForgeableMeasurable)
                    {
                        ((IForgeableMeasurable) outCap).setMetalAmount(((IForgeableMeasurable) inCap).getMetalAmount());
                    }
                }

            }
            return out;
        }
        return EMPTY;
    }
}

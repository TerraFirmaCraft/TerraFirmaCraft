/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurable;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.util.forge.ForgeRule;

/*
 * Use this AnvilRecipe implementation if your recipe has metal amount to transfer from input to output
 * *Or* if your input must have a specific amount to work
 */
public class AnvilMeasurableRecipe extends AnvilRecipe
{

    protected int metalAmount;
    protected boolean isCopyRule;


    /*
     * Use this constructor to make a recipe where only accepts input if it has a specific amount of metal.
     */
    public AnvilMeasurableRecipe(ResourceLocation name, ItemStack input, ItemStack output, int specificAmount, Metal.Tier minTier, ForgeRule... rules) throws IllegalArgumentException
    {
        super(name, input, output, minTier, rules);
        this.isCopyRule = false;
        this.metalAmount = specificAmount;
    }

    /*
     * Use this constructor to build a recipe where the metal amount is copied from input to output.
     */
    public AnvilMeasurableRecipe(ResourceLocation name, ItemStack input, ItemStack output, Metal.Tier minTier, ForgeRule... rules) throws IllegalArgumentException
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
            IForgeable cap = input.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
            if (cap instanceof IForgeableMeasurable) metalAmount = ((IForgeableMeasurable) cap).getMetalAmount();
            return true;
        }
        else
        {
            IForgeable cap = input.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
            if (cap instanceof IForgeableMeasurable)
                return metalAmount == ((IForgeableMeasurable) cap).getMetalAmount();
            return false;
        }
    }

    @Override
    @Nonnull
    public ItemStack getOutput()
    {
        ItemStack out = super.getOutput();
        if (isCopyRule)
        {
            IForgeable cap = out.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
            ((IForgeableMeasurable) cap).setMetalAmount(metalAmount);
        }
        return out;
    }
}

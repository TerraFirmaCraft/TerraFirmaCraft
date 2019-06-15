package net.dries007.tfc.api.recipes;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurable;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.util.forge.ForgeRule;

public class AnvilMeasurableRecipe extends AnvilRecipe
{

    protected int metalAmount;

    public AnvilMeasurableRecipe(ResourceLocation name, ItemStack input, ItemStack output, Metal.Tier minTier, ForgeRule... rules) throws IllegalArgumentException
    {
        super(name, input, output, minTier, rules);
        metalAmount = 100;
    }

    @Override
    public boolean matches(ItemStack input)
    {
        if(!super.matches(input))return false;
        //On match, save metalAmount to transfer it to the output
        IForgeable cap = input.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if(cap instanceof IForgeableMeasurable)metalAmount = ((IForgeableMeasurable)cap).getMetalAmount();
        return true;
    }

    @Nonnull
    public ItemStack getOutput()
    {
        ItemStack out = super.getOutput();
        IForgeable cap = input.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if(cap instanceof IForgeableMeasurable)((IForgeableMeasurable)cap).setMetalAmount(metalAmount);
        return out;
    }
}

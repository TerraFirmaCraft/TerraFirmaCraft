package net.dries007.tfc.api.recipes;

import javax.annotation.Nonnull;

import net.dries007.tfc.TerraFirmaCraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurable;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.util.forge.ForgeRule;

/*
 * Use this AnvilRecipe implementation if your ItemStack has metal amount to be split into smaller ItemStacks of itself
 */
public class AnvilSplitableRecipe extends AnvilMeasurableRecipe
{

    protected int splitAmount;

    public AnvilSplitableRecipe(ResourceLocation name, ItemStack splitItem, int splitAmount, Metal.Tier minTier, ForgeRule... rules) throws IllegalArgumentException
    {
        super(name, splitItem, splitItem, minTier, rules);
        this.splitAmount = splitAmount;
    }

    @Override
    public boolean matches(ItemStack input)
    {
        if(!super.matches(input))return false;
        //Splitable if the output is at least two(don't change this or you will have duplicates)
        return metalAmount > splitAmount;
    }

    @Nonnull
    public NonNullList<ItemStack> consumeInput(ItemStack input)
    {
        int surplus = metalAmount % splitAmount;
        int outCount = metalAmount / splitAmount;
        outCount--; //This is because one of the split stack will be left inside using getOutput() method
        //Also need to keep one in getOutput() method so Guis can show what recipe is selected.
        NonNullList<ItemStack> output = NonNullList.create();
        for(int i = 0; i<outCount;i++)
        {
            ItemStack dump = input.copy();
            IForgeable cap = dump.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
            cap.setWork(0); //Reset work without resetting temp
            cap.setRecipe((ResourceLocation)null);
            ((IForgeableMeasurable)cap).setMetalAmount(splitAmount);
            output.add(dump);
        }
        if(surplus > 0){
            ItemStack dumpSurplus = input.copy();
            IForgeable cap = dumpSurplus.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
            cap.setWork(0); //Reset work without resetting temp
            cap.setRecipe((ResourceLocation)null);
            ((IForgeableMeasurable)cap).setMetalAmount(surplus);
            output.add(dumpSurplus);
        }
        return output;
    }

    @Override
    @Nonnull
    public ItemStack getOutput()
    {
        ItemStack out = super.getOutput();
        IForgeable cap = out.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        ((IForgeableMeasurable) cap).setMetalAmount(splitAmount);
        return out;
    }
}

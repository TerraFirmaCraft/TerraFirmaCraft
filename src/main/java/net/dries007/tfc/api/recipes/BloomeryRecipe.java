/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurableMetal;
import net.dries007.tfc.api.capability.metal.CapabilityMetalItem;
import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.ItemsTFC;

public class BloomeryRecipe extends IForgeRegistryEntry.Impl<BloomeryRecipe>
{
    @Nullable
    public static BloomeryRecipe get(ItemStack inputItem)
    {
        return TFCRegistries.BLOOMERY.getValuesCollection().stream().filter(x -> x.isValidInput(inputItem)).findFirst().orElse(null);
    }

    private Metal metal; // Melting metal (which will be stored in a bloom)
    private IIngredient<ItemStack> additive; // The additive used in the process (charcoal is the default for iron)

    public BloomeryRecipe(@Nonnull Metal metal, IIngredient<ItemStack> additive)
    {
        this.metal = metal;
        this.additive = additive;

        //Ensure one bloomery recipe per metal
        //noinspection ConstantConditions
        setRegistryName(metal.getRegistryName());
    }

    public ItemStack getOutput(List<ItemStack> inputs)
    {
        int metalAmount = 0;
        for (ItemStack stack : inputs)
        {
            IMetalItem metalItem = CapabilityMetalItem.getMetalItem(stack);
            if (metalItem != null)
            {
                metalAmount += metalItem.getSmeltAmount(stack);
            }
        }
        ItemStack bloom = new ItemStack(ItemsTFC.UNREFINED_BLOOM);
        IForgeable cap = bloom.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if (cap instanceof IForgeableMeasurableMetal)
        {
            ((IForgeableMeasurableMetal) cap).setMetalAmount(metalAmount);
            ((IForgeableMeasurableMetal) cap).setMetal(metal);
            cap.setTemperature(cap.getMeltTemp() - 1);
        }
        return bloom;
    }

    /**
     * Used in JEI, gets a bloom with 100 units
     *
     * @return Bloom itemstack containing 100 units
     */
    public ItemStack getOutput()
    {
        ItemStack bloom = new ItemStack(ItemsTFC.UNREFINED_BLOOM);
        IForgeable cap = bloom.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if (cap instanceof IForgeableMeasurableMetal)
        {
            ((IForgeableMeasurableMetal) cap).setMetalAmount(100);
            ((IForgeableMeasurableMetal) cap).setMetal(metal);
            cap.setTemperature(cap.getMeltTemp() - 1);
        }
        return bloom;
    }

    public boolean isValidInput(ItemStack inputItem)
    {
        IMetalItem metalItem = CapabilityMetalItem.getMetalItem(inputItem);
        return metalItem != null && metalItem.getMetal(inputItem) == metal;
    }

    public boolean isValidAdditive(ItemStack input)
    {
        return additive.testIgnoreCount(input);
    }
}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.heat;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.objects.fluids.FluidsTFC;

@ParametersAreNonnullByDefault
public class HeatRecipeMetalMelting extends HeatRecipe
{
    @Nullable
    private static IMetalObject getMetalObject(ItemStack stack)
    {
        if (stack.getItem() instanceof IMetalObject)
        {
            return (IMetalObject) stack.getItem();
        }
        else if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof IMetalObject)
        {
            return (IMetalObject) ((ItemBlock) stack.getItem()).getBlock();
        }
        return null;
    }

    public HeatRecipeMetalMelting(Metal metal)
    {
        super(input -> {
            IMetalObject metalObject = getMetalObject(input);
            if (metalObject != null)
            {
                return metalObject.getMetal(input) == metal;
            }
            return false;
        }, metal.getMeltTemp(), metal.getTier());
    }

    @Nullable
    @Override
    public FluidStack getOutputFluid(ItemStack input)
    {
        IMetalObject metalObject = getMetalObject(input);
        if (metalObject != null)
        {
            Metal metal = metalObject.getMetal(input);
            if (metal != null && metalObject.canMelt(input))
            {
                return new FluidStack(FluidsTFC.getMetalFluid(metal), metalObject.getSmeltAmount(input));
            }
        }
        return null;
    }
}

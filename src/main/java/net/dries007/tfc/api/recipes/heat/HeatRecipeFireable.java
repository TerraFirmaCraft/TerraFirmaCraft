/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.heat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.util.IFireable;

@ParametersAreNonnullByDefault
public class HeatRecipeFireable extends HeatRecipe
{
    @Nullable
    private static IFireable getFireable(ItemStack stack)
    {
        if (stack.getItem() instanceof IFireable)
        {
            return (IFireable) stack.getItem();
        }
        else if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof IFireable)
        {
            return (IFireable) ((ItemBlock) stack.getItem()).getBlock();
        }
        return null;
    }

    public HeatRecipeFireable()
    {
        super(input -> getFireable(input) != null, 1599f);
    }

    @Override
    @Nonnull
    public ItemStack getOutputStack(ItemStack input)
    {
        if (input.getItem() instanceof IFireable)
        {
            return ((IFireable) input.getItem()).getFiringResult(input);
        }
        else if (input.getItem() instanceof ItemBlock && ((ItemBlock) input.getItem()).getBlock() instanceof IFireable)
        {
            return ((IFireable) ((ItemBlock) input.getItem()).getBlock()).getFiringResult(input);
        }
        TerraFirmaCraft.getLog().warn("A recipe that specified to use IFireable was supplied with an input that did not match! This is most likely caused my a badly specified recipe!");
        return ItemStack.EMPTY;
    }
}

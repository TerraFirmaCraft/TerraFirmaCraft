/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.util.IFireable;

/**
 * Pit kiln recipe, for simple itemstack -> itemstack conversions in the pit kiln
 *
 * todo: in 1.13+ move this to a json recipe type
 */
public class PitKilnRecipe extends IForgeRegistryEntry.Impl<PitKilnRecipe>
{
    @Nullable
    public static PitKilnRecipe get(ItemStack stack)
    {
        return TFCRegistries.PIT_KILN.getValuesCollection().stream().filter(r -> r.isValidInput(stack)).findFirst().orElse(null);
    }

    private final Ingredient ingredient;
    private final ItemStack output;
    private final IFireable fireable;

    public PitKilnRecipe(Ingredient ingredient, ItemStack output)
    {
        this.ingredient = ingredient;
        this.output = output;

        if (output.getItem() instanceof IFireable)
        {
            this.fireable = (IFireable) output.getItem();
        }
        else if (output.getItem() instanceof ItemBlock && ((ItemBlock) output.getItem()).getBlock() instanceof IFireable)
        {
            this.fireable = (IFireable) ((ItemBlock) output.getItem()).getBlock();
        }
        else
        {
            this.fireable = null;
        }
    }

    public boolean isValidInput(ItemStack input)
    {
        return ingredient.test(input);
    }

    public ItemStack getOutput(ItemStack input, Metal.Tier tier)
    {
        if (fireable != null)
        {
            return fireable.getFiringResult(input, tier);
        }
        return output.copy();
    }
}

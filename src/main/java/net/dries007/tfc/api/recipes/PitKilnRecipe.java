/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.IFireable;

/**
 * Pit kiln recipe, for simple itemstack -> itemstack conversions in the pit kiln
 * todo: in 1.13+ move this to a json recipe type
 */
@ParametersAreNonnullByDefault
public class PitKilnRecipe extends IForgeRegistryEntry.Impl<PitKilnRecipe>
{
    @Nullable
    public static PitKilnRecipe get(ItemStack stack)
    {
        return TFCRegistries.PIT_KILN.getValuesCollection().stream().filter(r -> r.isValidInput(stack)).findFirst().orElse(null);
    }

    private final IIngredient<ItemStack> ingredient;
    private final ItemStack output;
    private final boolean isComplex;

    public PitKilnRecipe(IIngredient<ItemStack> ingredient)
    {
        // Only for complex recipes
        this(ingredient, ItemStack.EMPTY, true);
    }

    public PitKilnRecipe(IIngredient<ItemStack> ingredient, ItemStack output)
    {
        this(ingredient, output, false);
    }

    private PitKilnRecipe(IIngredient<ItemStack> ingredient, ItemStack output, boolean isComplex)
    {
        this.ingredient = ingredient;
        this.output = output;
        this.isComplex = isComplex;
    }

    public boolean isValidInput(ItemStack input)
    {
        return ingredient.test(input);
    }

    public ItemStack getOutput(ItemStack input, Metal.Tier tier)
    {
        if (isComplex)
        {
            if (input.getItem() instanceof IFireable)
            {
                return ((IFireable) input.getItem()).getFiringResult(input, tier);
            }
            else if (input.getItem() instanceof ItemBlock && ((ItemBlock) input.getItem()).getBlock() instanceof IFireable)
            {
                return ((IFireable) ((ItemBlock) input.getItem()).getBlock()).getFiringResult(input, tier);
            }
            TerraFirmaCraft.getLog().warn("A recipe that specified to use IFireable was supplied with an input that did not match! This is most likely caused my a badly specified recipe!");
        }
        return output.copy();
    }
}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.heat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

/**
 * Generic recipe for items that heat up and transform
 * Used by pit kilns, fire pit, and charcoal forge for item transformations
 * Note! This recipe only supports inputs of stack size = 1, as they get "transformed" independent of their size.
 * As of currently, all inventories that use this recipe also have a stack size limit of 1, which is as intended
 */
@ParametersAreNonnullByDefault
public abstract class HeatRecipe extends IForgeRegistryEntry.Impl<HeatRecipe>
{
    @Nullable
    public static HeatRecipe get(ItemStack stack, Metal.Tier tier)
    {
        return TFCRegistries.HEAT.getValuesCollection().stream().filter(r -> r.isValidInput(stack, tier)).findFirst().orElse(null);
    }

    protected final IIngredient<ItemStack> ingredient;
    protected final Metal.Tier minTier;
    protected final float transformTemp;

    protected HeatRecipe(IIngredient<ItemStack> ingredient, Metal.Tier minTier, float transformTemp)
    {
        this.ingredient = ingredient;
        this.minTier = minTier;
        this.transformTemp = transformTemp;
    }

    /**
     * Use this to check if the recipe matches the input.
     * Since querying the recipe is somewhat intensive (i.e. not a do every tick thing), cache the recipe and only re-check on input change
     * Check if the recipe is hot enough to complete with {@link HeatRecipe#isValidTemperature(float)}
     *
     * @param input the input
     * @param tier  the tier of the device doing the heating
     * @return true if the recipe matches the input and tier
     */
    public boolean isValidInput(ItemStack input, Metal.Tier tier)
    {
        return tier.isAtLeast(minTier) && ingredient.test(input);
    }

    /**
     * @param temperature a temperature
     * @return true if the recipe should melt / transform at this temperature
     */
    public boolean isValidTemperature(float temperature)
    {
        return temperature >= transformTemp;
    }

    /**
     * Gets the output item. This output will be placed in the same slot if possible (charcoal forge), or an output slot if not (fire pit)
     * If EMPTY is returned, then this recipe produces no special output
     *
     * @param input the input stack
     * @return the stack to replace the input with
     */
    @Nonnull
    public ItemStack getOutputStack(ItemStack input)
    {
        return ItemStack.EMPTY;
    }

    @Nullable
    public FluidStack getOutputFluid(ItemStack input)
    {
        return null;
    }
}

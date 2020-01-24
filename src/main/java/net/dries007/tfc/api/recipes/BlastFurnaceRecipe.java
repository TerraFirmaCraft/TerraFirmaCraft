/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

public class BlastFurnaceRecipe extends IForgeRegistryEntry.Impl<BlastFurnaceRecipe>
{
    @Nullable
    public static BlastFurnaceRecipe get(ItemStack inputItem)
    {
        return TFCRegistries.BLAST_FURNACE.getValuesCollection().stream().filter(x -> x.isValidInput(inputItem)).findFirst().orElse(null);
    }

    protected Metal output;
    protected Function<ItemStack, Integer> supplier;
    protected IIngredient<ItemStack> additive;

    /**
     * Creates a new blast furnace recipe
     *
     * @param output   the metal output of this recipe
     * @param supplier a function to return how much metal content a itemstack can wield (return 0 if ingredient is not meltable to this metal)
     * @param additive additive to make this recipe (for pig iron, this means flux)
     */
    public BlastFurnaceRecipe(ResourceLocation name, Metal output, Function<ItemStack, Integer> supplier, IIngredient<ItemStack> additive)
    {
        this.output = output;
        this.supplier = supplier;
        this.additive = additive;

        setRegistryName(name);
    }

    public FluidStack getOutput(ItemStack stack)
    {
        return new FluidStack(FluidsTFC.getFluidFromMetal(output), supplier.apply(stack));
    }

    public boolean isValidInput(ItemStack stack)
    {
        return supplier.apply(stack) > 0;
    }

    public boolean isValidAdditive(ItemStack stack)
    {
        return additive.testIgnoreCount(stack);
    }
}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.compat.jei.IJEISimpleRecipe;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

public class LoomRecipe extends IForgeRegistryEntry.Impl<LoomRecipe> implements IJEISimpleRecipe
{
    @Nullable
    public static LoomRecipe get(ItemStack item)
    {
        return TFCRegistries.LOOM.getValuesCollection().stream().filter(x -> x.isValidInput(item)).findFirst().orElse(null);
    }

    private final IIngredient<ItemStack> inputItem;
    private final ItemStack outputItem;
    private final int stepCount;
    private final ResourceLocation inProgressTexture;

    public LoomRecipe(ResourceLocation name, IIngredient<ItemStack> input, ItemStack output, int stepsRequired, ResourceLocation inProgressTexture)
    {
        this.inputItem = input;
        this.outputItem = output;
        this.stepCount = stepsRequired;
        this.inProgressTexture = inProgressTexture;

        if (inputItem == null || input.getAmount() == 0 || outputItem == null || stepsRequired == 0)
        {
            throw new IllegalArgumentException("Input and output are not allowed to be empty");
        }
        setRegistryName(name);
    }

    public int getInputCount()
    {
        return inputItem.getAmount();
    }

    public int getStepCount()
    {
        return stepCount;
    }

    public ItemStack getOutputItem()
    {
        return outputItem.copy();
    }

    public ResourceLocation getInProgressTexture()
    {
        return inProgressTexture;
    }

    @Override
    public NonNullList<IIngredient<ItemStack>> getIngredients()
    {
        return NonNullList.withSize(1, inputItem);
    }

    @Override
    public NonNullList<ItemStack> getOutputs()
    {
        return NonNullList.withSize(1, outputItem);
    }

    private boolean isValidInput(ItemStack inputItem)
    {
        return this.inputItem.testIgnoreCount(inputItem);
    }

}

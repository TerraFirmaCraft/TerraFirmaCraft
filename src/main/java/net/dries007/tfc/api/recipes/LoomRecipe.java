/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

public class LoomRecipe extends IForgeRegistryEntry.Impl<LoomRecipe>
{
    @Nullable
    public static LoomRecipe get(ItemStack item)
    {
        return TFCRegistries.LOOM.getValuesCollection().stream().filter(x -> x.isValidInput(item)).findFirst().orElse(null);
    }

    private IIngredient<ItemStack> inputItem;
    private int inputAmount;
    private ItemStack outputItem;
    private int stepCount;
    private ResourceLocation inProgressTexture;

    public LoomRecipe(ResourceLocation name, IIngredient<ItemStack> input, int inputAmount, ItemStack output, int stepsRequired, ResourceLocation inProgressTexture)
    {
        this.inputItem = input;
        this.inputAmount = inputAmount;
        this.outputItem = output;
        this.stepCount = stepsRequired;
        this.inProgressTexture = inProgressTexture;

        if (inputItem == null || inputAmount == 0 || outputItem == null || stepsRequired == 0)
        {
            throw new IllegalArgumentException("Input and output are not allowed to be empty");
        }
        setRegistryName(name);
    }

    public int getInputCount()
    {
        return inputAmount;
    }

    public int getStepCount()
    {
        return stepCount;
    }

    public ItemStack getOutputItem()
    {
        return outputItem;
    }

    public ResourceLocation getInProgressTexture()
    {
        return inProgressTexture;
    }

    private boolean isValidInput(ItemStack inputItem)
    {
        return this.inputItem.testIgnoreCount(inputItem);
    }

}

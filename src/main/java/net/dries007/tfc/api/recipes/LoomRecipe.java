/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public class LoomRecipe extends IForgeRegistryEntry.Impl<LoomRecipe>
{
    @Nullable
    public static LoomRecipe get(Item item)
    {
        return TFCRegistries.LOOM.getValuesCollection().stream().filter(x -> x.isValidInput(item)).findFirst().orElse(null);
    }

    @Nullable
    public static LoomRecipe getByOutput(Item item)
    {
        return TFCRegistries.LOOM.getValuesCollection().stream().filter(x -> x.isValidOutput(item)).findFirst().orElse(null);
    }

    private Item inputItem;
    private int inputAmount;
    private Item outputItem;
    private int stepCount;
    private ResourceLocation inProgressTexture;

    public LoomRecipe(ResourceLocation name, Item input, int inputAmount, Item output, int stepsRequired, ResourceLocation inProgressTexture)
    {
        inputItem = input;
        this.inputAmount = inputAmount;
        outputItem = output;
        stepCount = stepsRequired;

        this.inProgressTexture = inProgressTexture;

        if (inputItem == null || inputAmount == 0 || outputItem == null || stepsRequired == 0)
            throw new IllegalArgumentException("Input and output are not allowed to be empty");
        setRegistryName(name);
    }

    public boolean isValidInput(Item inputItem)
    {
        return this.inputItem == inputItem;
    }

    public boolean isValidOutput(Item outputItem)
    {
        return this.outputItem == outputItem;
    }

    public int getInputCount()
    {
        return inputAmount;
    }

    public int getStepCount()
    {
        return stepCount;
    }

    public Item getOutputItem()
    {
        return outputItem;
    }

    public ResourceLocation getInProgressTexture()
    {
        return inProgressTexture;
    }

}

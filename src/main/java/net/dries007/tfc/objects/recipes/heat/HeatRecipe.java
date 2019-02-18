/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes.heat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

public class HeatRecipe
{
    private final InputType inputType;
    private final OutputType outputType;
    private ItemStack outputStack;
    private IMetalObject outputMetal;
    private ItemStack inputStack;
    private String inputOre;

    // Single item stack input and output
    public HeatRecipe(ItemStack outputStack, ItemStack inputStack)
    {
        IItemHeat cap = inputStack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        if (cap == null)
            throw new IllegalArgumentException("The input stack must implement IItemHeat capability for it to be used in a heat recipe!");

        this.outputStack = outputStack;
        this.inputStack = inputStack;

        inputType = InputType.ITEM;
        outputType = OutputType.ITEM;
    }

    // Ore Dictionary input, item stack output
    public HeatRecipe(ItemStack outputStack, String inputOre)
    {
        this.outputStack = outputStack;
        this.inputOre = inputOre;

        inputType = InputType.ORE_DICT;
        outputType = OutputType.ITEM;
    }

    // Single item stack input, Liquid metal output
    public HeatRecipe(IMetalObject outputMetal, ItemStack inputStack)
    {
        IItemHeat cap = inputStack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        if (cap == null)
            throw new IllegalArgumentException("The input stack must implement IItemHeat capability for it to be used in a heat recipe!");

        this.outputMetal = outputMetal;
        this.inputStack = inputStack;

        inputType = InputType.ITEM;
        outputType = OutputType.METAL;
    }

    // Single item stack input, Liquid metal + item stack output
    public HeatRecipe(IMetalObject outputMetal, ItemStack outputStack, ItemStack inputStack)
    {
        IItemHeat cap = inputStack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        if (cap == null)
            throw new IllegalArgumentException("The input stack must implement IItemHeat capability for it to be used in a heat recipe!");

        this.outputMetal = outputMetal;
        this.outputStack = outputStack;
        this.inputStack = inputStack;

        inputType = InputType.ITEM;
        outputType = OutputType.METAL_AND_ITEM;
    }

    @Nullable
    public ItemStack getOutputStack()
    {
        return outputType != OutputType.METAL ? outputStack.copy() : null;
    }

    @Nullable
    public FluidStack getOutputMetal(ItemStack stack)
    {
        if (outputType == OutputType.ITEM)
        {
            return null;
        }
        Metal metal = outputMetal.getMetal(stack);
        return metal != null ? new FluidStack(FluidsTFC.getMetalFluid(metal), outputMetal.getSmeltAmount(stack)) : null;
    }

    @Nonnull
    public ItemStack consumeInput(ItemStack input)
    {
        return Helpers.consumeItem(input, 1);
    }

    public boolean matchesInput(ItemStack stack)
    {
        if (inputType == InputType.ITEM)
        {
            return stack.isItemEqual(inputStack);
        }
        else
        {
            return OreDictionaryHelper.doesStackMatchOre(stack, inputOre);
        }
    }

    private enum OutputType
    {
        METAL,
        ITEM,
        METAL_AND_ITEM
    }

    private enum InputType
    {
        ITEM,
        ORE_DICT
    }
}

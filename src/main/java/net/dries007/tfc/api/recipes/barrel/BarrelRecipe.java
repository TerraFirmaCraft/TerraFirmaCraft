/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.barrel;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.Helpers;

public class BarrelRecipe extends IForgeRegistryEntry.Impl<BarrelRecipe>
{
    @Nullable
    public static BarrelRecipe get(ItemStack stack, FluidStack fluidStack)
    {
        return TFCRegistries.BARREL.getValuesCollection().stream().filter(x -> x.isValidInput(fluidStack, stack) && x.getDuration() != 0).findFirst().orElse(null);
    }

    @Nullable
    public static BarrelRecipe getInstant(ItemStack stack, FluidStack fluidStack)
    {
        return TFCRegistries.BARREL.getValuesCollection().stream().filter(x -> x.isValidInput(fluidStack, stack) && x.getDuration() == 0).findFirst().orElse(null);
    }

    /**
     * Checks if a fluidstack is an ingredient for any recipe
     * Used as a complement to barrel's whitelist
     */
    public static boolean isBarrelFluid(FluidStack fluidStack)
    {
        return TFCRegistries.BARREL.getValuesCollection().stream().filter(x -> x.inputFluid.testIgnoreCount(fluidStack)).findFirst().orElse(null) != null;
    }

    protected final IIngredient<ItemStack> inputStack;
    protected final IIngredient<FluidStack> inputFluid;
    private final FluidStack outputFluid;
    private final ItemStack outputStack;
    private final int duration;

    /**
     * Creates a barrel recipe
     *
     * @param inputFluid  fluid ingredients used to check if it is valid on barrel seal
     * @param inputStack  item ingredients used to check if it is valid on barrel seal
     * @param outputFluid the output fluid, when this recipe is completed
     * @param outputStack the output stack, when this recipe is completed
     * @param duration    the duration, in ticks, for this recipe to complete. 0 = Instant while negative means this recipe is infinite (for custom recipes that wait for unseal)
     */
    public BarrelRecipe(@Nonnull IIngredient<FluidStack> inputFluid, @Nonnull IIngredient<ItemStack> inputStack, @Nullable FluidStack outputFluid, @Nonnull ItemStack outputStack, int duration)
    {
        this.inputStack = inputStack;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
        this.outputStack = outputStack;
        this.duration = duration;
    }

    public boolean isValidInput(@Nullable FluidStack inputFluid, ItemStack inputStack)
    {
        return this.inputFluid.test(inputFluid) && this.inputStack.test(inputStack);
    }

    public boolean isValidInputInstant(ItemStack inputStack, @Nullable FluidStack inputFluid)
    {
        // Used on instant recipes, to verify that they only convert if there exists enough items to fully convert the fluid
        return inputFluid == null || inputFluid.amount / this.inputFluid.getAmount() <= inputStack.getCount() / this.inputStack.getAmount();
    }

    public int getDuration()
    {
        return duration;
    }

    /**
     * Only for GUI purposes - not intended as a crafting mechanic
     *
     * @return The output fluid stack
     */
    @Nullable
    public FluidStack getOutputFluid()
    {
        return outputFluid != null ? outputFluid.copy() : null;
    }

    /**
     * Only for GUI purposes - not intended as a crafting mechanic
     *
     * @return the output item stack
     */
    @Nonnull
    public ItemStack getOutputStack()
    {
        return outputStack;
    }

    @Nonnull
    public IIngredient<ItemStack> getItemIngredient()
    {
        return inputStack;
    }

    @Nonnull
    public IIngredient<FluidStack> getFluidIngredient()
    {
        return inputFluid;
    }

    @Nullable
    public FluidStack getOutputFluid(FluidStack inputFluid, ItemStack inputStack)
    {
        int multiplier = getMultiplier(inputFluid, inputStack);
        if (outputFluid != null)
        {
            // Ignore input and replace with output
            int outputAmount = Math.min(multiplier * outputFluid.amount, ConfigTFC.Devices.BARREL.tank);
            return new FluidStack(outputFluid.getFluid(), outputAmount);
        }
        else
        {
            // Try and keep as much of the original input as possible
            int retainAmount = inputFluid.amount - (multiplier * this.inputFluid.getAmount());
            if (retainAmount > 0)
            {
                return new FluidStack(inputFluid.getFluid(), inputFluid.amount - (multiplier * this.inputFluid.getAmount()));
            }
        }
        return null;
    }

    @Nonnull
    public List<ItemStack> getOutputItem(FluidStack inputFluid, ItemStack inputStack)
    {
        int multiplier = getMultiplier(inputFluid, inputStack);
        List<ItemStack> outputList = new ArrayList<>();
        if (!this.outputStack.isEmpty())
        {
            // Ignore input and replace with output
            int outputCount = multiplier * outputStack.getCount();
            do
            {
                int count = Math.min(outputCount, outputStack.getMaxStackSize());
                ItemStack output = outputStack.copy();
                output.setCount(count);
                outputCount -= count;
                outputList.add(output);
            } while (outputCount > 0);
        }
        else
        {
            // Try and keep as much of the original input as possible
            int retainCount = inputStack.getCount() - (multiplier * this.inputStack.getAmount());
            if (retainCount > 0)
            {
                inputStack.setCount(retainCount);
                outputList.add(inputStack);
            }
            else
            {
                outputList.add(ItemStack.EMPTY);
            }
        }
        return outputList;
    }

    /**
     * Called by TEBarrel when a recipe finishes
     * Used if you want to play a sound / cause an update of some sort
     *
     * @param world The world
     * @param pos   The TE pos
     */
    public void onRecipeComplete(World world, BlockPos pos) {}

    /**
     * Called by TEBarrel when the barrel just sealed
     * Use this if you want to do something to the input (like add a food trait) when the recipe "just started"
     *
     * You shouldn't consume the input here (use on unsealed if needed), otherwise, you're gonna keep consuming when the barrel reloads
     *
     * @param inputFluid the fluid that is in the barrel when it got sealed
     * @param inputStack the stack that is in the barrel when it got sealed
     */
    public void onBarrelSealed(FluidStack inputFluid, ItemStack inputStack) {}


    /**
     * Called by TEBarrel when the barrel is unsealed mid operation (the recipe didn't finish)
     * Use this if you want to do something to the input (like remove a food trait, or consume part of it) when the recipe is "broken"
     *
     * @param inputFluid the fluid that was in the barrel when the recipe "broke"
     * @param inputStack the stack that was in the barrel when the recipe "broke"
     * @return a fluid to replace the input when the barrel is unsealed
     */
    @Nullable
    public FluidStack getOutputFluidOnUnseal(FluidStack inputFluid, ItemStack inputStack)
    {
        return inputFluid;
    }

    /**
     * Called by TEBarrel when the barrel is unsealed mid operation (the recipe didn't finish)
     * Use this if you want to do something to the input (like remove a food trait, or consume part of it) when the recipe is "broken"
     *
     * @param inputFluid the fluid that was in the barrel when the recipe "broke"
     * @param inputStack the stack that was in the barrel when the recipe "broke"
     * @return a list of items to replace the input when the barrel is unsealed
     */
    @Nonnull
    public List<ItemStack> getOutputItemOnUnseal(FluidStack inputFluid, ItemStack inputStack)
    {
        return Helpers.listOf(inputStack);
    }

    /**
     * Gets the name of the recipe, to be displayed in the gui
     *
     * @return the name of the item stack produced, or the fluid produced, or a custom name if needed
     */
    @SideOnly(Side.CLIENT)
    public String getResultName()
    {
        ItemStack resultStack = getOutputStack();
        if (!resultStack.isEmpty())
        {
            return resultStack.getDisplayName();
        }
        else
        {
            FluidStack fluid = getOutputFluid();
            if (fluid == null)
            {
                return "???";
            }
            else
            {
                return fluid.getFluid().getLocalizedName(fluid);
            }
        }
    }

    protected int getMultiplier(FluidStack inputFluid, ItemStack inputStack)
    {
        if (isValidInput(inputFluid, inputStack))
        {
            if (this.inputFluid.getAmount() == 0)
            {
                return inputStack.getCount() / this.inputStack.getAmount();
            }
            else if (this.inputStack.getAmount() == 0)
            {
                return inputFluid.amount / this.inputFluid.getAmount();
            }
            else
            {
                return Math.min(inputFluid.amount / this.inputFluid.getAmount(), inputStack.getCount() / this.inputStack.getAmount());
            }
        }
        return 0;
    }
}

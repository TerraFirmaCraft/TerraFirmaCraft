/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Recipe type for all cooking pot recipes
 */
public interface IPotRecipe extends ISimpleRecipe<FluidInventoryRecipeWrapper>
{
    /**
     * Used to 'catch' bad inputs before execution (ie rotten food)
     */
    default boolean isValid(ItemStackHandler input, FluidStack fluid)
    {
        return true;
    }

    /**
     * Is the temperature valid to boil?
     */
    boolean isValidTemperature(float tempIn);

    /**
     * @return Number of ticks needed to boil for. Number resets internally if conditions not met
     */
    int getDuration();

    /**
     * Gets the output state (see below)
     */
    IPotRecipe.Output getOutput(ItemStackHandler input, FluidStack fluid);

    /**
     * @return Copy of the FluidStack to be filled in the cooking pot on completion
     */
    FluidStack getOutputFluid();

    /**
     * @return Copy of the ItemStacks to be put into the inventory on completion
     */
    NonNullList<ItemStack> getOutputItems();

    /**
     * Serializable 'output state' that gets stored in the tile.
     * Ideally this is called *once*, the data stored in NBT, and the data will
     * reload itself in the normal TE functions until it runs out,
     * at which point it will set itself to null (and allow boiling again)
     */
    interface Output extends INBTSerializable<CompoundNBT>
    {
        /**
         * @return If there's nothing left to return. Causes the output state to become null.
         */
        default boolean isEmpty()
        {
            return true;
        }

        /**
         * @return if we want to render a reddish soup fluid even if there's no actual fluid output
         */
        default boolean renderDefaultFluid()
        {
            return false;
        }

        /**
         * Called on right click when output is nonnull. Use to distribute output items.
         */
        default void onExtract(World world, BlockPos pos, ItemStack clickedWith)
        {

        }

        default CompoundNBT serializeNBT()
        {
            return new CompoundNBT();
        }

        default void deserializeNBT(CompoundNBT nbt)
        {

        }
    }

    @Override
    default IRecipeType<?> getType()
    {
        return TFCRecipeTypes.POT;
    }

    @Override
    default ItemStack getResultItem()
    {
        return ItemStack.EMPTY;
    }
}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import static net.minecraftforge.fluids.FluidUtil.getFluidHandler;

public class FluidTransferHelper
{
    /**
     * Tries to empty a fluid container item into the fluid handler, then stores the remainder in the given inventory.
     * If the fluid can't be transferred into the tank or the remainder can't fit into the return inventory, the action will be aborted.
     *
     * @param container        The filled fluid container ItemStack to empty.
     *                         Will not be modified directly, if modifications are necessary a modified copy is returned in the result.
     * @param fluidDestination The fluid destination to fill from the fluid container.
     * @param returnInventory  An inventory where empty containers are put.
     * @param returnSlot       The slot id into which the empty containers are inserted.
     * @param maxAmount        Maximum amount of fluid to take from the container.
     * @param world            World to play the sound in. Null for no sound.
     * @param pos              BlockPos to play the sound at. Null for no sound.
     * @return A {@link FluidActionResult} holding the result and the resulting container. The resulting container is empty on failure.
     */
    @Nonnull
    public static FluidActionResult emptyContainerIntoTank(@Nonnull ItemStack container, IFluidHandler fluidDestination, IItemHandler returnInventory, int returnSlot, int maxAmount, @Nullable World world, @Nullable BlockPos pos)
    {
        if (!container.isEmpty())
        {
            FluidActionResult emptiedSimulated = tryEmptyContainer(container, fluidDestination, maxAmount, false, null, null);

            if (emptiedSimulated.isSuccess())
            {
                // check if we can give the itemStack to the inventory
                ItemStack remainder = returnInventory.insertItem(returnSlot, emptiedSimulated.getResult(), true);

                if (remainder.isEmpty())
                {
                    FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, true, world, pos);
                    returnInventory.insertItem(returnSlot, emptiedReal.getResult(), false);

                    ItemStack containerCopy = container.copy();
                    containerCopy.shrink(1);

                    return new FluidActionResult(containerCopy);
                }
            }
        }

        return FluidActionResult.FAILURE;
    }

    /**
     * Tries to fill a fluid container item from the fluid handler, then stores the result in the given inventory.
     * If the fluid can't be transferred into the container or the result can't fit into the return inventory, the action will be aborted.
     *
     * @param container       The fluid container ItemStack to fill.
     *                        Will not be modified directly, if modifications are necessary a modified copy is returned in the result.
     * @param fluidSource     The fluid source to fill from.
     * @param returnInventory An inventory where filled containers are put.
     * @param returnSlot      The slot id into which the filled containers are inserted.
     * @param maxAmount       Maximum amount of fluid to put into the container.
     * @param world           World to play the sound in. Null for no sound.
     * @param pos             BlockPos to play the sound at. Null for no sound.
     * @return A {@link FluidActionResult} holding the result and the resulting container. The resulting container is empty on failure.
     */
    @Nonnull
    public static FluidActionResult fillContainerFromTank(@Nonnull ItemStack container, IFluidHandler fluidSource, IItemHandler returnInventory, int returnSlot, int maxAmount, @Nullable World world, @Nullable BlockPos pos)
    {
        FluidActionResult filledSimulated = tryFillContainer(container, fluidSource, maxAmount, false, null, null);

        if (filledSimulated.isSuccess())
        {
            // check if we can give the itemStack to the inventory
            ItemStack remainder = returnInventory.insertItem(returnSlot, filledSimulated.getResult(), true);

            if (remainder.isEmpty())
            {
                FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, true, world, pos);
                returnInventory.insertItem(returnSlot, filledReal.getResult(), false);

                ItemStack containerCopy = container.copy();
                containerCopy.shrink(1);
                return new FluidActionResult(containerCopy);
            }
        }

        return FluidActionResult.FAILURE;
    }

    /**
     * Takes a filled container and tries to empty it into the given tank.
     *
     * @param container        The filled container. Will not be modified.
     *                         Separate handling must be done to reduce the stack size, stow containers, etc, on success.
     * @param fluidDestination The fluid handler to be filled by the container.
     * @param maxAmount        The largest amount of fluid that should be transferred.
     * @param doDrain          True if the container should actually be drained, false if it should be simulated.
     * @param world            World to play the sound in. Null for no sound.
     * @param pos              BlockPos to play the sound at. Null for no sound.
     * @return A {@link FluidActionResult} holding the empty container if the fluid handler was filled.
     * NOTE If the container is consumable, the empty container will be null on success.
     */
    @Nonnull
    private static FluidActionResult tryEmptyContainer(@Nonnull ItemStack container, IFluidHandler fluidDestination, int maxAmount, boolean doDrain, @Nullable World world, @Nullable BlockPos pos)
    {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        IFluidHandlerItem containerFluidHandler = FluidUtil.getFluidHandler(containerCopy);
        if (containerFluidHandler != null)
        {
            if (doDrain)
            {
                FluidStack transfer = tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, true);
                if (transfer != null)
                {
                    if (world != null && pos != null)
                    {
                        world.playSound(null, pos, transfer.getFluid().getEmptySound(transfer), SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }

                    ItemStack resultContainer = containerFluidHandler.getContainer();
                    return new FluidActionResult(resultContainer);
                }
            }
            else
            {
                FluidStack simulatedTransfer = tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, false);
                if (simulatedTransfer != null)
                {
                    containerFluidHandler.drain(simulatedTransfer, true);
                    ItemStack resultContainer = containerFluidHandler.getContainer();
                    return new FluidActionResult(resultContainer);
                }
            }
        }
        return FluidActionResult.FAILURE;
    }

    /**
     * Fill a container from the given fluidSource.
     *
     * @param container   The container to be filled. Will not be modified.
     *                    Separate handling must be done to reduce the stack size, stow containers, etc, on success.
     * @param fluidSource The fluid handler to be drained.
     * @param maxAmount   The largest amount of fluid that should be transferred.
     * @param doFill      true if the container should actually be filled, false if it should be simulated.
     * @param world       World to play the sound in. Null for no sound.
     * @param pos         BlockPos to play the sound at. Null for no sound.
     * @return A {@link FluidActionResult} holding the filled container if successful.
     */
    @Nonnull
    private static FluidActionResult tryFillContainer(@Nonnull ItemStack container, IFluidHandler fluidSource, int maxAmount, boolean doFill, @Nullable World world, @Nullable BlockPos pos)
    {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        IFluidHandlerItem containerFluidHandler = getFluidHandler(containerCopy);
        if (containerFluidHandler != null)
        {
            FluidStack simulatedTransfer = FluidUtil.tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, false);
            if (simulatedTransfer != null)
            {
                if (doFill)
                {
                    FluidUtil.tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, true);

                    if (world != null && pos != null)
                    {
                        world.playSound(null, pos, simulatedTransfer.getFluid().getFillSound(simulatedTransfer), SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                }
                else
                {
                    containerFluidHandler.fill(simulatedTransfer, true);
                }

                ItemStack resultContainer = containerFluidHandler.getContainer();
                return new FluidActionResult(resultContainer);
            }
        }
        return FluidActionResult.FAILURE;
    }

    /**
     * Fill a destination fluid handler from a source fluid handler with a max amount.
     * To transfer as much as possible, use {@link Integer#MAX_VALUE} for maxAmount.
     *
     * @param fluidDestination The fluid handler to be filled.
     * @param fluidSource      The fluid handler to be drained.
     * @param maxAmount        The largest amount of fluid that should be transferred.
     * @param doTransfer       True if the transfer should actually be done, false if it should be simulated.
     * @return the fluidStack that was transferred from the source to the destination. null on failure.
     */
    @Nullable
    private static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, int maxAmount, boolean doTransfer)
    {
        FluidStack drainable = fluidSource.drain(maxAmount, false);
        if (drainable != null && drainable.amount > 0)
        {
            return tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer);
        }
        return null;
    }

    /**
     * Internal method for filling a destination fluid handler from a source fluid handler using a specific fluid.
     * Assumes that "drainable" can be drained from "fluidSource".
     * or {@link #tryFluidTransfer(IFluidHandler, IFluidHandler, int, boolean)}.
     */
    @Nullable
    private static FluidStack tryFluidTransfer_Internal(IFluidHandler fluidDestination, IFluidHandler fluidSource, FluidStack drainable, boolean doTransfer)
    {
        int fillableAmount = fluidDestination.fill(drainable, false);
        if (fillableAmount > 0)
        {
            if (doTransfer)
            {
                FluidStack drained = fluidSource.drain(fillableAmount, true);
                if (drained != null)
                {
                    drained.amount = fluidDestination.fill(drained, true);
                    return drained;
                }
            }
            else
            {
                FluidStack drained = fluidSource.drain(fillableAmount, false);
                if (drained != null)
                {
                    drainable.amount = fillableAmount;
                    return drainable;
                }
            }
        }
        return null;
    }
}

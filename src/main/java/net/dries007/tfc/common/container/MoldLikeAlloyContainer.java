/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.MoldLike;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import org.jetbrains.annotations.Nullable;

public class MoldLikeAlloyContainer extends ItemStackContainer implements ISlotCallback
{
    public static MoldLikeAlloyContainer create(ItemStack stack, InteractionHand hand, Inventory playerInv, int windowId)
    {
        return new MoldLikeAlloyContainer(stack, hand, playerInv, windowId).init(playerInv);
    }

    @Nullable private final MoldLike mold;
    private final IItemHandlerModifiable inventory;

    private MoldLikeAlloyContainer(ItemStack stack, InteractionHand hand, Inventory playerInv, int windowId)
    {
        super(TFCContainerTypes.MOLD_LIKE_ALLOY.get(), windowId, playerInv, stack, hand);

        this.mold = MoldLike.get(stack);
        this.inventory = new InventoryItemHandler(this, 1);
    }

    @Override
    public void broadcastChanges()
    {
        // This is *basically* a tick method, it is invoked on each server player tick
        // So, we abuse that fact and use it to transfer fluid, one mB each tick, to the inner inventory
        if (mold != null && mold.isMolten())
        {
            final ItemStack outputStack = inventory.getStackInSlot(0);
            outputStack.getCapability(Capabilities.FLUID).ifPresent(outputFluidCap -> {
                final FluidStack drained = mold.drain(1, IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty())
                {
                    final int filled = outputFluidCap.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                    if (filled == 1)
                    {
                        // Execute the prior drain, and copy temperature
                        mold.drain(1, IFluidHandler.FluidAction.EXECUTE);
                        outputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(outputHeatCap -> outputHeatCap.setTemperatureIfWarmer(mold.getTemperature()));
                    }
                }
            });
        }
        super.broadcastChanges();
    }

    @Override
    public void removed(Player player)
    {
        if (!player.level.isClientSide())
        {
            final ItemStack stack = inventory.getStackInSlot(0);
            if (!stack.isEmpty())
            {
                ItemHandlerHelper.giveItemToPlayer(player, stack);
            }
        }
        super.removed(player);
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return mold != null && (mold.isMolten() || mold.getFluidInTank(0).isEmpty()); // Don't close instantly as soon as the mold is empty.
    }

    @Override
    protected void addContainerSlots()
    {
        if (mold != null)
        {
            addSlot(new CallbackSlot(this, inventory, 0, 80, 34));
        }
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return stack.getCapability(Capabilities.FLUID).isPresent();
    }
}

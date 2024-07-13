/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import java.util.Objects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.MoldLike;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.Metal;

public class MoldLikeAlloyContainer extends ItemStackContainer implements ISlotCallback
{
    public static MoldLikeAlloyContainer create(ItemStack stack, InteractionHand hand, int slot, Inventory playerInv, int windowId)
    {
        return new MoldLikeAlloyContainer(stack, hand, slot, playerInv, windowId).init(playerInv);
    }

    @Nullable private final MoldLike mold;
    private final IItemHandlerModifiable inventory;

    private MoldLikeAlloyContainer(ItemStack stack, InteractionHand hand, int slot, Inventory playerInv, int windowId)
    {
        super(TFCContainerTypes.MOLD_LIKE_ALLOY.get(), windowId, playerInv, stack, hand, slot);

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
            final @Nullable IFluidHandler outputFluidCap = outputStack.getCapability(Capabilities.FluidHandler.ITEM);
            if (outputFluidCap != null)
            {
                final FluidStack drained = mold.drain(1, IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty())
                {
                    final int filled = outputFluidCap.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                    if (filled == 1)
                    {
                        final Metal metal = Objects.requireNonNullElse(Metal.get(drained.getFluid()), Metal.unknown());
                        final @Nullable IHeat outputHeat = HeatCapability.get(outputStack);
                        final float heatCapacityOf1mB = metal.heatCapacity(1);

                        // Execute the prior drain, and adjust temperature
                        mold.drain(1, IFluidHandler.FluidAction.EXECUTE);
                        if (outputHeat != null)
                        {
                            outputHeat.addTemperatureFromSourceWithHeatCapacity(mold.getTemperature(), heatCapacityOf1mB);
                        }
                    }
                }
            }
        }
        super.broadcastChanges();
    }

    @Override
    public void removed(Player player)
    {
        if (!player.level().isClientSide())
        {
            final ItemStack stack = inventory.getStackInSlot(0);
            if (!stack.isEmpty())
            {
                giveItemStackToPlayerOrDrop(player, stack);
            }
        }
        super.removed(player);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return mold != null && (mold.isMolten() || mold.getFluidInTank(0).isEmpty()) && super.stillValid(player); // Don't close instantly as soon as the mold is empty.
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, 1, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
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
        return Helpers.mightHaveCapability(stack, Capabilities.FluidHandler.ITEM);
    }

    public IItemHandler getInventory()
    {
        return inventory;
    }
}

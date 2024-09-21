/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.component.mold.Vessel;
import net.dries007.tfc.common.container.slot.ImmutableItemHandlerSlot;
import net.dries007.tfc.common.items.VesselItem;

public class SmallVesselInventoryContainer extends ItemStackContainer
{
    public static SmallVesselInventoryContainer create(ItemStack stack, InteractionHand hand, int slot, Inventory playerInv, int windowId)
    {
        return new SmallVesselInventoryContainer(stack, hand, slot, playerInv, windowId).init(playerInv);
    }

    @Nullable private final Vessel vessel;

    private SmallVesselInventoryContainer(ItemStack stack, InteractionHand hand, int slot, Inventory playerInv, int windowId)
    {
        super(TFCContainerTypes.SMALL_VESSEL_INVENTORY.get(), windowId, playerInv, stack, hand, slot);

        vessel = Vessel.get(stack);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return vessel != null && vessel.isInventory() && super.stillValid(player);
    }

    /**
     * In {@link net.minecraft.world.inventory.AbstractContainerMenu#doClick(int, int, ClickType, Player)} there is a call path through which
     * {@link net.minecraft.world.inventory.Slot#onTake(Player, ItemStack)} is not called. It just directly sets the slot, and the carried
     * in the container.
     * <p>
     * We call the callback's slotless version here, as it's all we can realistically do.
     *
     * @param stack The unsealedStack that is set to be carried.
     */
    @Override
    public void setCarried(ItemStack stack)
    {
        if (vessel != null) vessel.onTake(stack);
        super.setCarried(stack);
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
        {
            case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, VesselItem.SLOTS, false);
            case CONTAINER -> {
                // Remove the preserved trait, pre-emptively, if the unsealedStack were to be transferred out. If any remains, then re-apply it.
                FoodCapability.removeTrait(stack, FoodTraits.PRESERVED);
                boolean result = !moveItemStackTo(stack, containerSlots, slots.size(), false);
                if (result)
                {
                    FoodCapability.applyTrait(stack, FoodTraits.PRESERVED);
                }
                yield result;
            }
        };
    }

    @Override
    protected void addContainerSlots()
    {
        if (vessel != null)
        {
            addSlot(new ImmutableItemHandlerSlot(vessel, 0, 71, 23));
            addSlot(new ImmutableItemHandlerSlot(vessel, 1, 89, 23));
            addSlot(new ImmutableItemHandlerSlot(vessel, 2, 71, 41));
            addSlot(new ImmutableItemHandlerSlot(vessel, 3, 89, 41));
        }
    }
}

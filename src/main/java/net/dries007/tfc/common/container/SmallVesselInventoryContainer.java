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

import net.dries007.tfc.common.capabilities.VesselLike;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.dries007.tfc.common.items.VesselItem;
import org.jetbrains.annotations.Nullable;

public class SmallVesselInventoryContainer extends ItemStackContainer
{
    public static SmallVesselInventoryContainer create(ItemStack stack, InteractionHand hand, Inventory playerInv, int windowId)
    {
        return new SmallVesselInventoryContainer(stack, hand, playerInv, windowId).init(playerInv);
    }

    @Nullable private final VesselLike vessel;

    private SmallVesselInventoryContainer(ItemStack stack, InteractionHand hand, Inventory playerInv, int windowId)
    {
        super(TFCContainerTypes.SMALL_VESSEL_INVENTORY.get(), windowId, playerInv, stack, hand);

        callback = vessel = VesselLike.get(stack);
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return vessel != null && vessel.mode() == VesselLike.Mode.INVENTORY && vessel.getTemperature() == 0;
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, VesselItem.SLOTS, false);
                case CONTAINER -> {
                    // Remove the preserved trait, pre-emptively, if the stack were to be transferred out. If any remains, then re-apply it.
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
        assert vessel != null;

        addSlot(new CallbackSlot(vessel, vessel, 0, 71, 23));
        addSlot(new CallbackSlot(vessel, vessel, 1, 89, 23));
        addSlot(new CallbackSlot(vessel, vessel, 2, 71, 41));
        addSlot(new CallbackSlot(vessel, vessel, 3, 89, 41));
    }
}

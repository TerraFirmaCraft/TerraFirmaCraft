/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.tileentity.FirepitTileEntity;

import static net.dries007.tfc.common.tileentity.AbstractFirepitTileEntity.SLOT_FUEL_INPUT;

public class FirepitContainer extends BlockEntityContainer<FirepitTileEntity>
{
    public static FirepitContainer create(FirepitTileEntity tile, Inventory playerInv, int windowId)
    {
        return new FirepitContainer(tile, windowId).init(playerInv, 20);
    }

    private FirepitContainer(FirepitTileEntity tile, int windowId)
    {
        super(TFCContainerTypes.FIREPIT.get(), windowId, tile);

        addDataSlots(tile.getSyncableData());
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, SLOT_FUEL_INPUT, FirepitTileEntity.SLOT_ITEM_INPUT + 1, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            // fuel slots
            for (int i = 0; i < 4; i++)
            {
                addSlot(new CallbackSlot(blockEntity, handler, i, 8, 70 - 18 * i));
            }
            addSlot(new CallbackSlot(blockEntity, handler, FirepitTileEntity.SLOT_ITEM_INPUT, 80, 29));
            addSlot(new CallbackSlot(blockEntity, handler, FirepitTileEntity.SLOT_OUTPUT_1, 71, 57));
            addSlot(new CallbackSlot(blockEntity, handler, FirepitTileEntity.SLOT_OUTPUT_2, 89, 57));
        });
    }
}

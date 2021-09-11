/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.tileentity.GrillTileEntity;

import static net.dries007.tfc.common.tileentity.GrillTileEntity.*;

public class GrillContainer extends BlockEntityContainer<GrillTileEntity>
{
    public static GrillContainer create(GrillTileEntity grill, Inventory playerInv, int windowId)
    {
        return new GrillContainer(grill, windowId).init(playerInv, 20);
    }

    private GrillContainer(GrillTileEntity grill, int windowId)
    {
        super(TFCContainerTypes.GRILL.get(), windowId, grill);

        addDataSlots(grill.getSyncableData());
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, SLOT_FUEL_INPUT, SLOT_FUEL_INPUT + 1, false) && !moveItemStackTo(stack, SLOT_FUEL_INPUT + 1, SLOT_FUEL_INPUT + 6, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            for (int i = 0; i < 4; i++) // Fuel
            {
                addSlot(new CallbackSlot(blockEntity, handler, i, 8, 70 - 18 * i));
            }
            for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++) // Grill input
            {
                addSlot(new CallbackSlot(blockEntity, handler, i, 62 + (i - SLOT_EXTRA_INPUT_START) * 18, 20));
            }
        });
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.tileentity.PotTileEntity;

import static net.dries007.tfc.common.tileentity.AbstractFirepitTileEntity.SLOT_FUEL_INPUT;
import static net.dries007.tfc.common.tileentity.GrillTileEntity.SLOT_EXTRA_INPUT_END;
import static net.dries007.tfc.common.tileentity.GrillTileEntity.SLOT_EXTRA_INPUT_START;

public class PotContainer extends BlockEntityContainer<PotTileEntity>
{
    public static PotContainer create(PotTileEntity pot, Inventory playerInv, int windowId)
    {
        return new PotContainer(pot, windowId).init(playerInv, 20);
    }

    private PotContainer(PotTileEntity pot, int windowId)
    {
        super(TFCContainerTypes.POT.get(), windowId, pot);

        addDataSlots(pot.getSyncableData());
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, SLOT_FUEL_INPUT, SLOT_FUEL_INPUT + 1, false) &&
                    !moveItemStackTo(stack, SLOT_FUEL_INPUT + 1, SLOT_FUEL_INPUT + 6, false);
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
            for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++) // Pot input
            {
                addSlot(new CallbackSlot(blockEntity, handler, i, 62 + (i - SLOT_EXTRA_INPUT_START) * 18, 20));
            }
        });
    }
}

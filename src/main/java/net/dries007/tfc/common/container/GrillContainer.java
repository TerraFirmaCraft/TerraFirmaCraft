/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.tileentity.GrillTileEntity;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.tileentity.GrillTileEntity.*;

public class GrillContainer extends TileEntityContainer<GrillTileEntity>
{
    private static final Logger LOGGER = LogManager.getLogger();

    public GrillContainer(GrillTileEntity tile, Inventory playerInv, int windowId)
    {
        super(TFCContainerTypes.GRILL.get(), tile, playerInv, windowId);

        addDataSlots(tile.getSyncableData());
    }

    @Override
    protected void addContainerSlots()
    {
        Helpers.ifPresentOrElse(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY), handler -> {
            for (int i = 0; i < 4; i++) // Fuel
            {
                addSlot(new CallbackSlot(tile, handler, i, 8, 70 - 18 * i));
            }
            for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++) // Grill input
            {
                addSlot(new CallbackSlot(tile, handler, i, 62 + (i - SLOT_EXTRA_INPUT_START) * 18, 20));
            }
        }, () -> LOGGER.warn("Missing capability on grill at {}?", tile.getBlockPos()));
    }

    @Override
    protected boolean transferStackIntoContainer(ItemStack stack, int containerSlots) // this uses index of the slots sequentially, not the slot IDs themselves
    {
        return !moveItemStackTo(stack, SLOT_FUEL_INPUT, SLOT_FUEL_INPUT + 1, false) && !moveItemStackTo(stack, SLOT_FUEL_INPUT + 1, SLOT_FUEL_INPUT + 6, false);
    }

    @Override
    protected void addPlayerInventorySlots(Inventory playerInv)
    {
        addPlayerInventorySlots(playerInv, 20);
    }
}

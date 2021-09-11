/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.tileentity.LogPileTileEntity;

public class LogPileContainer extends BlockEntityContainer<LogPileTileEntity>
{
    public static LogPileContainer create(LogPileTileEntity logPile, Inventory playerInventory, int windowId)
    {
        return new LogPileContainer(logPile, playerInventory, windowId).init(playerInventory);
    }

    public LogPileContainer(LogPileTileEntity logPile, Inventory playerInventory, int windowId)
    {
        super(TFCContainerTypes.LOG_PILE.get(), windowId, logPile);
        logPile.onOpen(playerInventory.player);
    }

    @Override
    public void removed(Player player)
    {
        blockEntity.onClose(player);
        super.removed(player);
    }

    @Override
    protected void addContainerSlots()
    {
        blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(handler -> {
            addSlot(new CallbackSlot(blockEntity, handler, 0, 71, 23));
            addSlot(new CallbackSlot(blockEntity, handler, 1, 89, 23));
            addSlot(new CallbackSlot(blockEntity, handler, 2, 71, 41));
            addSlot(new CallbackSlot(blockEntity, handler, 3, 89, 41));
        });
    }
}

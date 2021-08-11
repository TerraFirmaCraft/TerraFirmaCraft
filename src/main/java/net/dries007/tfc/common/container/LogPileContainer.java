/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.tileentity.LogPileTileEntity;
import net.dries007.tfc.util.Helpers;

public class LogPileContainer extends TileEntityContainer<LogPileTileEntity>
{
    private static final Logger LOGGER = LogManager.getLogger();

    public LogPileContainer(LogPileTileEntity tile, Inventory playerInventory, int windowId)
    {
        super(TFCContainerTypes.LOG_PILE.get(), tile, playerInventory, windowId);
        tile.onOpen(playerInventory.player);
    }

    @Override
    public void removed(Player player)
    {
        tile.onClose(player);
        super.removed(player);
    }

    @Override
    protected void addContainerSlots()
    {
        Helpers.ifPresentOrElse(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), handler -> {
            addSlot(new CallbackSlot(tile, handler, 0, 71, 23));
            addSlot(new CallbackSlot(tile, handler, 1, 89, 23));
            addSlot(new CallbackSlot(tile, handler, 2, 71, 41));
            addSlot(new CallbackSlot(tile, handler, 3, 89, 41));
        }, () -> LOGGER.warn("Missing capability on firepit at {}?", tile.getBlockPos()));
    }
}

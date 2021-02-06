package net.dries007.tfc.common.container;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.tileentity.LogPileTileEntity;
import net.dries007.tfc.util.Helpers;

public class LogPileContainer extends TileEntityContainer<LogPileTileEntity>
{
    private static final Logger LOGGER = LogManager.getLogger();

    public LogPileContainer(LogPileTileEntity tile, PlayerInventory playerInventory, int windowId)
    {
        super(TFCContainerTypes.LOG_PILE.get(), tile, playerInventory, windowId);
        tile.setContainerOpen(true);
    }

    @Override
    protected void addContainerSlots()
    {
        Helpers.ifPresentOrElse(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), handler -> {
            addSlot(new SlotCallback(tile, handler, 0, 71, 23));
            addSlot(new SlotCallback(tile, handler, 1, 89, 23));
            addSlot(new SlotCallback(tile, handler, 2, 71, 41));
            addSlot(new SlotCallback(tile, handler, 3, 89, 41));
        }, () -> LOGGER.warn("Missing capability on firepit at {}?", tile.getBlockPos()));
    }

    @Override
    public void removed(PlayerEntity player)
    {
        tile.setContainerOpen(false);
        super.removed(player);
    }
}

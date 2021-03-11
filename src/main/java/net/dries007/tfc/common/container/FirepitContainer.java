package net.dries007.tfc.common.container;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.common.tileentity.FirepitTileEntity;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.tileentity.FirepitTileEntity.*;

public class FirepitContainer extends TileEntityContainer<FirepitTileEntity>
{
    private static final Logger LOGGER = LogManager.getLogger();

    public FirepitContainer(FirepitTileEntity tile, PlayerInventory playerInv, int windowId)
    {
        super(TFCContainerTypes.FIREPIT.get(), tile, playerInv, windowId);

        addDataSlots(tile.getSyncableData());
    }

    @Override
    protected void addContainerSlots()
    {
        Helpers.ifPresentOrElse(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY), handler -> {
            // fuel slots
            for (int i = 0; i < 4; i++)
            {
                addSlot(new SlotCallback(tile, handler, i, 8, 70 - 18 * i));
            }
            addSlot(new SlotCallback(tile, handler, SLOT_ITEM_INPUT, 80, 28));
            addSlot(new SlotCallback(tile, handler, SLOT_OUTPUT_1, 71, 56));
            addSlot(new SlotCallback(tile, handler, SLOT_OUTPUT_2, 89, 56));
        }, () -> LOGGER.warn("Missing capability on firepit at {}?", tile.getBlockPos()));
    }

    @Override
    protected boolean transferStackIntoContainer(ItemStack stack, int containerSlots)
    {
        return !moveItemStackTo(stack, SLOT_FUEL_INPUT, SLOT_ITEM_INPUT + 1, false);
    }

    @Override
    protected void addPlayerInventorySlots(PlayerInventory playerInv)
    {
        addPlayerInventorySlots(playerInv, 20);
    }
}

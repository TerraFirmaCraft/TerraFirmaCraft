package net.dries007.tfc.common.container;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.tileentity.CharcoalForgeTileEntity;
import net.dries007.tfc.util.Helpers;

public class CharcoalForgeContainer extends TileEntityContainer<CharcoalForgeTileEntity>
{
    private static final Logger LOGGER = LogManager.getLogger();

    public CharcoalForgeContainer(CharcoalForgeTileEntity tile, PlayerInventory playerInventory, int windowId)
    {
        super(TFCContainerTypes.CHARCOAL_FORGE.get(), tile, playerInventory, windowId);

        addDataSlots(tile.getSyncableData());
    }

    @Override
    protected void addContainerSlots()
    {
        Helpers.ifPresentOrElse(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY), handler -> {
            // Fuel slots
            // Note: the order of these statements is important
            int index = CharcoalForgeTileEntity.SLOT_FUEL_MIN;
            addSlot(new CallbackSlot(tile, handler, index++, 80, 70));
            addSlot(new CallbackSlot(tile, handler, index++, 98, 52));
            addSlot(new CallbackSlot(tile, handler, index++, 62, 52));
            addSlot(new CallbackSlot(tile, handler, index++, 116, 34));
            addSlot(new CallbackSlot(tile, handler, index, 44, 34));

            // Input slots
            // Note: the order of these statements is important
            index = CharcoalForgeTileEntity.SLOT_INPUT_MIN;
            addSlot(new CallbackSlot(tile, handler, index++, 80, 52));
            addSlot(new CallbackSlot(tile, handler, index++, 98, 34));
            addSlot(new CallbackSlot(tile, handler, index++, 62, 34));
            addSlot(new CallbackSlot(tile, handler, index++, 116, 16));
            addSlot(new CallbackSlot(tile, handler, index, 44, 16));

            // Extra slots (for ceramic molds)
            for (int i = CharcoalForgeTileEntity.SLOT_EXTRA_MIN; i <= CharcoalForgeTileEntity.SLOT_EXTRA_MAX; i++)
            {
                addSlot(new CallbackSlot(tile, handler, i, 152, 16 + 18 * (i - CharcoalForgeTileEntity.SLOT_EXTRA_MIN)));
            }
        }, () -> LOGGER.warn("Missing capability on firepit at {}?", tile.getBlockPos()));
    }

    @Override
    protected boolean transferStackIntoContainer(ItemStack stack, int containerSlots)
    {
        return !moveItemStackTo(stack, CharcoalForgeTileEntity.SLOT_EXTRA_MIN, CharcoalForgeTileEntity.SLOT_EXTRA_MAX + 1, false)
            && !moveItemStackTo(stack, CharcoalForgeTileEntity.SLOT_FUEL_MIN, CharcoalForgeTileEntity.SLOT_INPUT_MAX + 1, false);
    }

    @Override
    protected void addPlayerInventorySlots(PlayerInventory playerInv)
    {
        addPlayerInventorySlots(playerInv, 20);
    }
}

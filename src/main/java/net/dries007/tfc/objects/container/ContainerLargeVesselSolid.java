/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.dries007.tfc.objects.inventory.slot.SlotCallback;
import net.dries007.tfc.objects.te.TELargeVessel;

import static net.dries007.tfc.objects.blocks.wood.BlockBarrel.SEALED;

public class ContainerLargeVesselSolid extends ContainerTE<TELargeVessel> implements IButtonHandler
{

    public ContainerLargeVesselSolid(InventoryPlayer playerInv, TELargeVessel tile)
    {
        super(playerInv, tile);
    }

    @Nullable
    public IItemHandler getLargeVesselInventory()
    {
        return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    public boolean isLargeVesselSealed()
    {
        return tile.isSealed();
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable NBTTagCompound extraNBT)
    {
        // Slot will always be 0, extraNBT will be empty
        if (!tile.getWorld().isRemote)
        {
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            tile.getWorld().setBlockState(tile.getPos(), state.withProperty(SEALED, !state.getValue(SEALED)));
            tile.onSealed();
        }
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        if (inventory != null)
        {
            for (int y = 0; y < 3; y++)
            {
                for (int x = 0; x < 3; x++)
                {
                    addSlotToContainer(new SolidSlot(inventory, x * 3 + y + 3, 34 + x * 18, 19 + y * 18, tile));
                }
            }
        }
    }

    public BlockPos getTilePos()
    {
        return tile.getPos();
    }

    private class SolidSlot extends SlotCallback
    {
        public SolidSlot(@Nonnull IItemHandler inventory, int idx, int x, int y, @Nonnull ISlotCallback callback)
        {
            super(inventory, idx, x, y, callback);
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack)
        {
            IItemSize sizeCap = CapabilityItemSize.getIItemSize(stack);
            if (sizeCap != null)
            {
                if (sizeCap.getSize(stack) != Size.HUGE)
                {
                    return true;
                }
                return false;
            }
            return true;
        }
    }
}

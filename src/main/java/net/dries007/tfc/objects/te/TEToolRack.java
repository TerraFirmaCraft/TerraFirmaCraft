/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;

import net.dries007.tfc.objects.items.metal.ItemMetalTool;

public class TEToolRack extends TileEntity
{
    /**
     * Modify this method to register items that can be put on a tool rack.
     * TODO: an api for other mods to register their items
     *
     * @param item item to check
     * @return true if this item can be put on a tool rack, false otherwise
     */
    public static boolean isItemEligible(Item item)
    {
        return item instanceof ItemMetalTool
            || item instanceof ItemTool
            || item instanceof ItemBow
            || item instanceof ItemHoe;
    }

    public static boolean isItemEligible(@Nullable ItemStack item)
    {
        if (item == null || item.isEmpty()) return false;
        return isItemEligible(item.getItem());
    }

    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

    public NonNullList<ItemStack> getItems()
    {
        return items;
    }

    public void onBreakBlock()
    {
        items.forEach(i -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), i));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        ItemStackHelper.loadAllItems(compound.getCompoundTag("items"), items);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("items", ItemStackHelper.saveAllItems(new NBTTagCompound(), items));
        return compound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 127, getUpdateTag());
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
        updateBlock();
    }

    public void updateBlock()
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3); // sync TE
        markDirty(); // make sure everything saves to disk
    }

    public boolean onRightClick(EntityPlayer player, EnumHand hand, int slot)
    {
        ItemStack slotItem = items.get(slot);
        ItemStack heldItem = player.getHeldItem(hand);
        //we take
        if (!slotItem.isEmpty())
        {
            //TODO: pit kilns should be this smart too.
            if (player.getHeldItem(hand).isEmpty())
                player.setHeldItem(hand, slotItem.splitStack(1));
            else
            {
                //check for the other hand
                if (hand == EnumHand.MAIN_HAND)
                    hand = EnumHand.OFF_HAND;
                else
                    hand = EnumHand.MAIN_HAND;
                if (player.getHeldItem(hand).isEmpty())
                    player.setHeldItem(hand, slotItem.splitStack(1));
                else
                    player.addItemStackToInventory(slotItem.splitStack(1));
            }
            items.set(slot, ItemStack.EMPTY);
            updateBlock();
            return true;
        }
        //we put
        if (isItemEligible(heldItem))
        {
            items.set(slot, player.isCreative() ?
                new ItemStack(heldItem.getItem(), 1, heldItem.getMetadata(), heldItem.getTagCompound())
                : heldItem.splitStack(1));
            updateBlock();
            return true;
        }
        return false;
    }
}

package net.dries007.tfc.objects.te;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TEItemHolder extends TileEntity
{
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        ItemStackHelper.loadAllItems(compound.getCompoundTag("items"), items);
    }

    @Override
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

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos(), getPos().add(1, 1, 1));
    }

    /**
     * @return true if an action was taken (passed back through onItemRightClick
     */
    public boolean onRightClick(EntityPlayer player, ItemStack stack, boolean x, boolean z)
    {
        final int slot = (x ? 1 : 0) + (z ? 2 : 0);

        // Try and extract an item
        if (stack.isEmpty())
        {

            // Try and grab the item
            ItemStack current = items.get(slot);
            if (current.isEmpty())
            {
                return false;
            }
            player.addItemStackToInventory(current.splitStack(1));
            items.set(slot, ItemStack.EMPTY);
            updateBlock();
            if (items.stream().filter(ItemStack::isEmpty).count() == 4)
            {
                world.setBlockToAir(pos);
            }
            return true;
        }
        else
        {
            // Insert an item

            if (items.get(slot).isEmpty())
            {
                items.set(slot, stack.splitStack(1));
                updateBlock();
                return true;
            }

        }
        return false;
    }

    public void onBreakBlock()
    {
        items.forEach(i -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), i));
    }

    public NonNullList<ItemStack> getItems()
    {
        return items;
    }

    private void updateBlock()
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3); // sync TE
        markDirty(); // make sure everything saves to disk
    }
}

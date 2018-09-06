/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.te;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class TEToolRack extends TileEntity
{
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "tool_rack");
    //todo: remove debug
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, new ItemStack(Block.getBlockById(1)));

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
        // TODO: FOR DEBUG SAKE. must be removed
        items.set(0, new ItemStack(Block.getBlockById(2)));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("items", ItemStackHelper.saveAllItems(new NBTTagCompound(), items));
        return compound;
    }

    public void updateBlock()
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3); // sync TE
        markDirty(); // make sure everything saves to disk
    }

    public void onRightClick(EntityPlayer player, ItemStack item, boolean x, boolean z)
    {
        int slot = 0;
        if (x) slot += 1;
        if (z) slot += 2;
        if (item.isEmpty())
        {
            ItemStack current = items.get(slot);
            if (current.isEmpty()) return;
            player.addItemStackToInventory(current.splitStack(1));
            items.set(slot, ItemStack.EMPTY);
            updateBlock();
        }
    }
}

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.dries007.tfc.Constants.MOD_ID;

public class TEWorldItem extends TileEntity
{
    public TEWorldItem(){ } //this has to be here otherwise it will fail to load somehow

    public TEWorldItem(World world){
        super();
        this.world = world;
    }
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "world_item");
    private ItemStack item = ItemStack.EMPTY;
    public void onBreakBlock()
    {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), item);
    }

    public void setItem(ItemStack item, boolean markDirty){
        this.item = item;
        if(world == null) return;
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 18); // sync TE
        if(markDirty)
        markDirty();
    }

    /*public void setItem(){

    }*/

    public ItemStack getItem(){
        return item;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);        setItem(new ItemStack(compound.getCompoundTag("Item")), false);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("Item", item.writeToNBT(new NBTTagCompound()));
        return compound;
    }

    @Override
    public void onLoad(){
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 18);
    }

    /*public void updateBlock()
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 18); // sync TE
        //markDirty(); // make sure everything saves to disk
    }*/

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 127, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return item.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        //readFromNBT(pkt.getNbtCompound());
        setItem(new ItemStack(pkt.getNbtCompound()), false);
        //updateBlock();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos().add(0.25D, 0D, 0.25D), getPos().add(0.75D, 0.0625D, 0.75D));//TODO:see block bounding box method
    }
}

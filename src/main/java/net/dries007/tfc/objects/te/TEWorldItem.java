/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import mcp.MethodsReturnNonnullByDefault;

import static net.dries007.tfc.Constants.MOD_ID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TEWorldItem extends TileEntity
{
    public ItemStackHandler inventory = new ItemStackHandler(1);/*{
        @Override
        protected void onContentsChanged(int slot) {
            if (!world.isRemote) {
                TerraFirmaCraft.getNetwork().sendToAllAround(new PacketUpdateWorldItem(TEWorldItem.this),
                    new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32));
            }
        }
    };*/

    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "world_item");
    //private ItemStack item = ItemStack.EMPTY;

    public TEWorldItem()
    {
        super();

        this.markDirty();
    }

    public void onBreakBlock()
    {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(0));
    }

    /*public void setItem(ItemStack item){
        this.item = item;
        //updateBlock(doMarkDirty);
        if(!world.isRemote)
        {
            this.markDirty();
            TerraFirmaCraft.getNetwork().sendToAllAround(new PacketUpdateWorldItem(this),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
    }*/

    /*public void setItem(){

    }*/

    /*public ItemStack getItem(){
        //if(item == ItemStack.EMPTY)
        //    updateBlock(false);
        return item;
    }*/

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        super.readFromNBT(compound);
        //ItemStack stack = new ItemStack(compound.getCompoundTag("Item"));
        //TerraFirmaCraft.getLog().debug("Trying to load state + "+compound.getCompoundTag("Item")+" and "+stack.getDisplayName());
        //if(stack != null && !stack.isEmpty())
        //    setItem(stack);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("inventory", inventory.serializeNBT());
        return super.writeToNBT(compound);
        //compound.setTag("Item", item.writeToNBT(new NBTTagCompound()));
        //return compound;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 1024.0D;
    }

    @Override
    public void onLoad(){
        //if (world.isRemote) {
        //    TerraFirmaCraft.getNetwork().sendToServer(new PacketRequestWorldItem(this));
        //}
        // updateBlock(false);
    }

    public NBTTagCompound getUpdatePacketTag(NBTTagCompound nbt)
    {
        nbt.setTag("inventory", inventory.serializeNBT());
        return nbt;
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        if (world != null)
        {
            return new SPacketUpdateTileEntity(this.getPos(), 0, this.getUpdatePacketTag(new NBTTagCompound()));
        }

        return null;
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        // The tag from this method is used for the initial chunk packet,
        // and it needs to have the TE position!
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("x", this.getPos().getX());
        nbt.setInteger("y", this.getPos().getY());
        nbt.setInteger("z", this.getPos().getZ());
        getUpdatePacketTag(nbt);
        //nbt.setTag("inventory", inventory.serializeNBT());

        // Add the per-block data to the tag
        return nbt;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        this.handleUpdateTag(packet.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        if (tag.hasKey("inventory"))
        {
            inventory.deserializeNBT(tag.getCompoundTag("inventory"));
        }
        //TerraFirmaCraft.getLog().debug("Got update packet!");
    }

    /*public void updateBlock(boolean doMarkDirty)
    {
        if(world == null) return;
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 2); // sync TE
        if(doMarkDirty)
        markDirty();
    }*/

    /*@Nullable
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
        setItem(new ItemStack(pkt.getNbtCompound()));
        //updateBlock();
    }*/

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos(), getPos().add(1D, 1D, 1D));//TODO:see block bounding box method
    }
}

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
    public ItemStackHandler inventory = new ItemStackHandler(1);

    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "world_item");

    public TEWorldItem()
    {
        super();

        this.markDirty();
    }

    public void onBreakBlock()
    {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(0));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("inventory", inventory.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 1024.0D;
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        // The tag from this method is used for the initial chunk packet, and it needs to have the TE position!
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("x", this.getPos().getX());
        nbt.setInteger("y", this.getPos().getY());
        nbt.setInteger("z", this.getPos().getZ());
        return getUpdatePacketTag(nbt);

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
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos(), getPos().add(1D, 1D, 1D));
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
    }

    private NBTTagCompound getUpdatePacketTag(NBTTagCompound nbt)
    {
        nbt.setTag("inventory", inventory.serializeNBT());
        return nbt;
    }
}

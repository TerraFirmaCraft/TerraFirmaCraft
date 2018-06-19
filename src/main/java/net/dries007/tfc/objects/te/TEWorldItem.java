/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import java.util.Random;
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
    private byte rotation;

    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "world_item");

    public TEWorldItem()
    {
        Random rand = new Random();
        rotation = (byte) rand.nextInt(4);
    }

    public void onBreakBlock()
    {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(0));
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        inventory.deserializeNBT(tag.getCompoundTag("inventory"));
        rotation = tag.hasKey("rotation") ? tag.getByte("rotation") : 0;
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setTag("inventory", inventory.serializeNBT());
        tag.setByte("rotation", rotation);
        return super.writeToNBT(tag);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 1024.0D;
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        if (world != null)
        {
            return new SPacketUpdateTileEntity(this.getPos(), 0, this.writeToNBT(new NBTTagCompound()));
        }

        return null;
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        // The tag from this method is used for the initial chunk packet, and it needs to have the TE position!
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("x", this.getPos().getX());
        nbt.setInteger("y", this.getPos().getY());
        nbt.setInteger("z", this.getPos().getZ());
        return writeToNBT(nbt);

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
        readFromNBT(tag);
    }

    public byte getRotation()
    {
        return rotation;
    }
}

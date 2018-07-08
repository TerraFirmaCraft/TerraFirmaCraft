/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.items.metal.ItemIngot;

import static net.dries007.tfc.Constants.MOD_ID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TEIngotPile extends TileEntity
{
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "ingot_pile");

    private Metal metal;
    private int count;

    public TEIngotPile()
    {
        metal = Metal.UNKNOWN;
        count = 1;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        metal = tag.hasKey("metal") ? Metal.valueOf(tag.getString("metal")) : Metal.UNKNOWN;
        count = tag.hasKey("count") ? tag.getInteger("count") : 1;
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setString("metal", metal.name());
        tag.setInteger("count", count);
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
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        this.handleUpdateTag(packet.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        readFromNBT(tag);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos(), getPos().add(1D, 1D, 1D));
    }

    public void onBreakBlock()
    {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(),
            new ItemStack(ItemIngot.get(metal, Metal.ItemType.INGOT), count));
    }

    public Metal getMetal()
    {
        return metal;
    }

    public void setMetal(Metal metal)
    {
        this.metal = metal;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }
}

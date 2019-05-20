/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.Constants;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TEPlacedItemFlat extends TEInventory
{
    private byte rotation;

    public TEPlacedItemFlat()
    {
        super(1);
        rotation = (byte) Constants.RNG.nextInt(4);
    }

    public void onBreakBlock(BlockPos pos)
    {
        Helpers.spawnItemStack(world, pos, inventory.getStackInSlot(0));
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        rotation = tag.hasKey("rotation") ? tag.getByte("rotation") : 0;
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
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
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos(), getPos().add(1D, 1D, 1D));
    }

    public byte getRotation()
    {
        return rotation;
    }
}

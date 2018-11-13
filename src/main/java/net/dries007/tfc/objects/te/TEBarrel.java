package net.dries007.tfc.objects.te;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class TEBarrel extends TileEntity
{
    public double fillHeightForRender;

    public FluidTank tank = new FluidTank(10000);

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        tank.readFromNBT(compound.getCompoundTag("tank"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound tankTag = new NBTTagCompound();

        tank.writeToNBT(tankTag);
        compound.setTag("tank", tankTag);

        return super.writeToNBT(compound);
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        this.readFromNBT(tag);

        if (tank.getFluid() != null)
        {
            fillHeightForRender = 0.140625D + (0.75D - 0.015625D) * tank.getFluidAmount() / tank.getCapacity();
        }
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
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        this.handleUpdateTag(packet.getNbtCompound());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }
}

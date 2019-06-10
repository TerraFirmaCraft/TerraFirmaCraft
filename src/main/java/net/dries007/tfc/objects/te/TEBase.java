/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * TE Implementation that syncs NBT on world / chunk load, and on block updates
 */
@ParametersAreNonnullByDefault
public abstract class TEBase extends TileEntity
{
    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        // Needed to sync TE data on block update
        // It only needs the TE-specific data
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new SPacketUpdateTileEntity(getPos(), 1, nbt);
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag()
    {
        // Needed to sync TE data on chunk load
        // This needs to write the TE data (i.e. x, y, z), as well as whatever TE-specific NBT is required
        NBTTagCompound nbt = super.getUpdateTag();
        return writeToNBT(nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        // Needed to sync TE data on chunk load
        NBTTagCompound nbt = pkt.getNbtCompound();
        readFromNBT(nbt);
    }

    @Override
    public void handleUpdateTag(NBTTagCompound nbt)
    {
        // Needed to sync TE data on chunk load
        readFromNBT(nbt);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }
}
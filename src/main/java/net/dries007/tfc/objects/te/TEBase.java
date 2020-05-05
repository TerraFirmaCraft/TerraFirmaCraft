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
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * TE Implementation that syncs NBT on world / chunk load, and on block updates
 */
@ParametersAreNonnullByDefault
public abstract class TEBase extends TileEntity
{
    /**
     * Gets the update packet that is used to sync the TE on load
     */
    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    /**
     * Gets the update tag send by packets. Contains base data (i.e. position), as well as TE specific data
     */
    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return nbt;
    }

    /**
     * Handles updating on client side when a block update is received
     */
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    /**
     * Reads the update tag attached to a chunk or TE packet
     */
    @Override
    public void handleUpdateTag(NBTTagCompound nbt)
    {
        readFromNBT(nbt);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    /**
     * Syncs the TE data to client via means of a block update
     * Use for stuff that is updated infrequently, for data that is analogous to changing the state.
     * DO NOT call every tick
     */
    public void markForBlockUpdate()
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        markDirty();
    }

    /**
     * Marks a tile entity for syncing without sending a block update.
     * Use preferentially over {@link TEBase#markForBlockUpdate()} if there's no reason to have a block update.
     * For container based integer synchronization, see ITileFields
     * DO NOT call every tick
     */
    public void markForSync()
    {
        sendVanillaUpdatePacket();
        markDirty();
    }

    private void sendVanillaUpdatePacket()
    {
        SPacketUpdateTileEntity packet = getUpdatePacket();
        if (packet != null && world instanceof WorldServer)
        {
            PlayerChunkMapEntry chunk = ((WorldServer) world).getPlayerChunkMap().getEntry(pos.getX() >> 4, pos.getZ() >> 4);
            if (chunk != null)
            {
                chunk.sendPacket(packet);
            }
        }
    }
}
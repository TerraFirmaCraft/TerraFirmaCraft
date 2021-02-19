/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;

public abstract class TFCTileEntity extends TileEntity
{
    protected static final Logger LOGGER = LogManager.getLogger();

    protected TFCTileEntity(TileEntityType<?> type)
    {
        super(type);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(getPos(), 1, write(new CompoundNBT()));
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return write(super.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        read(getBlockState(), pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt)
    {
        read(state, nbt);
    }

    /**
     * Syncs the TE data to client via means of a block update
     * Use for stuff that is updated infrequently, for data that is analogous to changing the state.
     * DO NOT call every tick
     */
    public void markForBlockUpdate()
    {
        if (world != null)
        {
            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(getPos(), state, state, 3);
            markDirty();
        }
    }

    /**
     * Marks a tile entity for syncing without sending a block update.
     * Use preferentially over {  InventoryTileEntity#markForBlockUpdate()} if there's no reason to have a block update.
     * For container based integer synchronization, see ITileFields
     * DO NOT call every tick
     */
    public void markForSync()
    {
        sendVanillaUpdatePacket();
        markDirtyFast();
    }

    /**
     * Marks the tile entity dirty without updating comparator output.
     * Useful when called a lot for TE's that don't have a comparator output
     */
    protected void markDirtyFast()
    {
        if (world != null)
        {
            getBlockState();
            world.setTileEntity(pos, this);
        }
    }

    protected void sendVanillaUpdatePacket()
    {
        SUpdateTileEntityPacket packet = getUpdatePacket();
        BlockPos pos = getPos();

        if (packet != null && world instanceof ServerWorld)
        {
            ((ServerChunkProvider) world.getChunkProvider()).chunkManager.getTrackingPlayers(new ChunkPos(pos), false).forEach(e -> e.connection.sendPacket(packet));
        }
    }
}

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
        return new SUpdateTileEntityPacket(getBlockPos(), 1, save(new CompoundNBT()));
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return save(super.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        load(getBlockState(), pkt.getTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt)
    {
        load(state, nbt);
    }

    /**
     * Syncs the TE data to client via means of a block update
     * Use for stuff that is updated infrequently, for data that is analogous to changing the state.
     * DO NOT call every tick
     */
    public void markForBlockUpdate()
    {
        if (level != null)
        {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
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
        setChanged();
    }

    /**
     * Marks the tile entity dirty without updating comparator output.
     * Useful when called a lot for TE's that don't have a comparator output
     */
    protected void markDirtyFast()
    {
        if (level != null)
        {
            getBlockState();
            level.blockEntityChanged(worldPosition, this);
        }
    }

    protected void sendVanillaUpdatePacket()
    {
        SUpdateTileEntityPacket packet = getUpdatePacket();
        BlockPos pos = getBlockPos();

        if (packet != null && level instanceof ServerWorld)
        {
            ((ServerChunkProvider) level.getChunkSource()).chunkMap.getPlayers(new ChunkPos(pos), false).forEach(e -> e.connection.send(packet));
        }
    }
}

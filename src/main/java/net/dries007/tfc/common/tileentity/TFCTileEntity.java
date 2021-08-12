/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;

public abstract class TFCTileEntity extends BlockEntity
{
    protected static final Logger LOGGER = LogManager.getLogger();

    protected TFCTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return new ClientboundBlockEntityDataPacket(getBlockPos(), 1, save(new CompoundTag()));
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return save(super.getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
    {
        load(packet.getTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag tag)
    {
        load(tag);
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
     * Use preferentially over {@link InventoryTileEntity#markForBlockUpdate()} if there's no reason to have a block update.
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
            level.blockEntityChanged(worldPosition);
        }
    }

    protected void sendVanillaUpdatePacket()
    {
        ClientboundBlockEntityDataPacket packet = getUpdatePacket();
        BlockPos pos = getBlockPos();

        if (packet != null && level instanceof ServerLevel)
        {
            ((ServerChunkCache) level.getChunkSource()).chunkMap.getPlayers(new ChunkPos(pos), false).forEach(e -> e.connection.send(packet));
        }
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TFCBlockEntity extends BlockEntity
{
    protected TFCBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    /**
     * @return The packet to send to the client upon block update. This is returned in client in {@link #onDataPacket(Connection, ClientboundBlockEntityDataPacket)}
     */
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /**
     * Handle a packet sent from {@link #getUpdatePacket()}. Delegates to {@link #handleUpdateTag(CompoundTag)}.
     */
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
    {
        if (packet.getTag() != null)
        {
            handleUpdateTag(packet.getTag());
        }
    }

    /**
     * Returns the tag containing information needed to send to the client, either on block update or on bulk chunk update. This tag is either returned with the packet in {@link #getUpdatePacket()} or {@link #handleUpdateTag(CompoundTag)} based on where it was called from.
     * Delegates to {@link #saveWithoutMetadata()} which calls {@link #saveAdditional(CompoundTag)}
     */
    @Override
    public CompoundTag getUpdateTag()
    {
        return saveWithoutMetadata();
    }

    /**
     * Handles an update tag sent from the server.
     * Delegates to {@link #load(CompoundTag)} which calls {@link #loadAdditional(CompoundTag)}
     */
    @Override
    public void handleUpdateTag(CompoundTag tag)
    {
        load(tag);
    }

    @Override
    public final void load(CompoundTag tag)
    {
        loadAdditional(tag);
        super.load(tag);
    }

    /**
     * Override to save block entity specific data.
     */
    @Override
    protected void saveAdditional(CompoundTag tag) {}

    /**
     * Override to load block entity specific data.
     */
    protected void loadAdditional(CompoundTag tag) {}

    /**
     * Syncs the block entity data to client via means of a block update.
     * Use for stuff that is updated infrequently, for data that is analogous to changing the state.
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
     * Marks a block entity for syncing without sending a block update. Also internally marks dirty.
     * Use preferentially over {@link InventoryBlockEntity#markForBlockUpdate()} if there's no reason to have a block update.
     */
    public void markForSync()
    {
        sendVanillaUpdatePacket();
        setChanged();
    }

    /**
     * Marks a block entity as dirty, without updating the comparator output. Use preferentially for updates that want to mark themselves as dirty every tick, and don't require updating comparator output.
     * Reimplements {@link net.minecraft.world.level.Level#blockEntityChanged(BlockPos)} due to trying to avoid comparator updates, called due to MinecraftForge#9169
     */
    @SuppressWarnings("deprecation")
    public void markDirty()
    {
        if (level != null && level.hasChunkAt(worldPosition))
        {
            level.getChunkAt(worldPosition).setUnsaved(true);
        }
    }

    public void sendVanillaUpdatePacket()
    {
        final ClientboundBlockEntityDataPacket packet = getUpdatePacket();
        final BlockPos pos = getBlockPos();
        if (packet != null && level instanceof ServerLevel serverLevel)
        {
            serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).forEach(e -> e.connection.send(packet));
        }
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import net.dries007.tfc.world.ChunkGeneratorExtension;

public final class ChunkDataAttachment
{
    public static final AttachmentType<ChunkData> TYPE = AttachmentType.builder(holder -> {
        final ChunkAccess chunk = (ChunkAccess) holder;
        final ChunkDataGenerator generator = chunk.getLevel() instanceof ServerLevel level
            && level.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension ex
                ? ex.chunkDataProvider().generator()
                : null;
        if (chunk.getLevel() instanceof ClientLevel)
        {
            ChunkData.dequeueClientChunkData(chunk.getPos());
        }
        return new ChunkData(generator, chunk.getPos());
    })
        .serialize(new IAttachmentSerializer<CompoundTag, ChunkData>() {
            @Override
            public ChunkData read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider)
            {
                final ChunkData data = holder.getData(TYPE);
                data.deserializeNBT(tag);
                return data;
            }

            @Override
            public CompoundTag write(ChunkData data, HolderLookup.Provider provider)
            {
                return data.serializeNBT();
            }
        })
        .build();

    public static ChunkData get(ChunkAccess chunk)
    {
        return chunk instanceof ImposterProtoChunk impostor ? get(impostor.getWrapped())
            : chunk instanceof EmptyLevelChunk ? ChunkData.EMPTY
            : chunk.getData(TYPE);
    }
}

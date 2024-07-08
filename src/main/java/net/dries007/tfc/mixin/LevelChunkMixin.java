/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dries007.tfc.world.ChunkGeneratorExtension;

/**
 * This is necessary to capture the {@code ProtoChunk} when promoting proto chunks to level chunks after world generation. At this time,
 * we copy chunk data from the proto chunk (stored via weak hash map on the {@code ChunkDataProvider}) to a capability stored on the
 * {@code LevelChunk}. However, capability initialization does not have access, so we initialize the capability empty, and replace its
 * contents after the fact.
 * <p>
 * In 1.21, with NeoForge supporting both Proto and Level chunk attachments, this should not be necessary as we can represent both forms
 * of the chunk data with an attachment and NeoForge will handle copying.
 */
@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin
{
    @Inject(method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V", at = @At("RETURN"))
    private void copyProtoChunkDataToLevelChunk(ServerLevel level, ProtoChunk chunk, LevelChunk.PostLoadProcessor postLoad, CallbackInfo ci)
    {
        if (level.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension ex)
        {
            ex.chunkDataProvider().promotePartial(chunk, (LevelChunk) (Object) this);
        }
    }
}

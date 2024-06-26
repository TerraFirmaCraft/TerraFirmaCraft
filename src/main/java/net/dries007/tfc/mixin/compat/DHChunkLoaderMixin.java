/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "loaderCommon.forge.com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.ChunkLoader")
public abstract class DHChunkLoaderMixin
{
    @Inject(
        method = "read",
        target = @Desc(value = "read", args={WorldGenLevel.class, ChunkPos.class, CompoundTag.class}, ret= LevelChunk.class),
        at = @At("RETURN"),
        require = 0
    )
    @Dynamic("method in Distant Horizon's chunk generator which creates LevelChunks without reading capability data")
    @SuppressWarnings("deprecation")
    private static void forceDHToReadCapabilities(WorldGenLevel level, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<LevelChunk> cir)
    {
        if (cir.getReturnValue() != null)
        {
            if (tag.contains("ForgeCaps")) cir.getReturnValue().readCapsFromNBT(tag.getCompound("ForgeCaps"));
        }
    }
}

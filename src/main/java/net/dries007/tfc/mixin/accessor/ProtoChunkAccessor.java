package net.dries007.tfc.mixin.accessor;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ProtoChunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProtoChunk.class)
public interface ProtoChunkAccessor
{
    @Accessor("levelHeightAccessor")
    LevelHeightAccessor accessor$getLevelHeightAccessor();
}

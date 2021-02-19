/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.world.gen.carver;

import java.util.BitSet;
import java.util.function.Function;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.CanyonWorldCarver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CanyonWorldCarver.class)
public interface CanyonWorldCarverAccessor
{
    /**
     * We need to override the generate method in order to change the height, but this method is not protected.
     */
    //gen Canyon
    @Invoker("func_227204_a_")
    void call$func_227204_a_(IChunk chunk, Function<BlockPos, Biome> biomeReader, long canyonSeed, int seaLevel, int chunkX, int chunkZ, double xOffset, double yOffset, double zOffset, float width, float yaw, float pitch, int branchMin, int branchMax, double three, BitSet carvingMask);
}

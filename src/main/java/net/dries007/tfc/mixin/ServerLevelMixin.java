/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level
{
    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, Holder<DimensionType> dimensionType, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed)
    {
        super(levelData, dimension, dimensionType, profiler, isClientSide, isDebug, biomeZoomSeed);
    }

    /**
     * Replace snow and ice generation, and thawing, with specialized versions.
     * Target the {@link java.util.Random#nextInt(int)} call which guards the snow and ice block.
     */
    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int onEnvironmentTick(Random random, int bound, LevelChunk chunk, int randomTickSpeed)
    {
        // Targeting the random.nextInt(16) only
        if (bound == 16)
        {
            EnvironmentHelpers.tickChunk((ServerLevel) (Object) this, chunk, getProfiler());
            return 1; // Prevent the normal snow and ice generation from happening
        }
        return random.nextInt(bound); // Default behavior
    }

    /**
     * Redirect a call to {@link Biome#getPrecipitation()} with one that has world and position context.
     * The position is inferred by reverse engineering {@link ServerLevel#getBlockRandomPos(int, int, int, int)}
     */
    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitation()Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    private Biome.Precipitation tickChunkRedirectGetPrecipitation(Biome biome, LevelChunk chunk)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final BlockPos pos = Helpers.getPreviousRandomPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), 15, randValue).below();
        return Climate.getPrecipitation(this, pos);
    }

    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;coldEnoughToSnow(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean tickChunkRedirectColdEnoughToSnow(Biome biome, BlockPos pos, LevelChunk chunk)
    {
        return Climate.coldEnoughToSnow(this, pos);
    }
}

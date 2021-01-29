/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.world.server;

import java.util.function.Supplier;

import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ISpawnWorldInfo;

import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World
{
    private ServerWorldMixin(ISpawnWorldInfo spawnWorldInfo, RegistryKey<World> registryKey, DimensionType dimensionType, Supplier<IProfiler> profiler, boolean meh, boolean bleh, long blah)
    {
        super(spawnWorldInfo, registryKey, dimensionType, profiler, meh, bleh, blah);
    }

    /**
     * Hook into chunk random ticks, allow for snow placement modification.
     * Could be replaced by https://github.com/MinecraftForge/MinecraftForge/pull/7235
     */
    @Inject(method = "tickChunk", at = @At("RETURN"))
    private void inject$tickChunk(Chunk chunkIn, int randomTickSpeed, CallbackInfo ci)
    {
        EnvironmentHelpers.onEnvironmentTick((ServerWorld) (Object) this, chunkIn, random);
    }

    /**
     * Redirect a call to {@link Biome#getPrecipitation()} with one that has world and position context.
     * The position is inferred by reverse engineering {@link ServerWorld#getBlockRandomPos(int, int, int, int)}
     */
    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;"))
    private Biome.RainType redirect$tickChunk$getPrecipitation(Biome biome, Chunk chunkIn)
    {
        ChunkPos chunkPos = chunkIn.getPos();
        BlockPos pos = Helpers.getPreviousRandomPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), 15, randValue).below();
        return Climate.getPrecipitation(this, pos);
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Random;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.EnvironmentHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin
{
    /**
     * Replace snow and ice generation, and thawing, with specialized versions.
     * Target the {@link java.util.Random#nextInt(int)} call which guards the snow and ice block.
     */
    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"), slice = @Slice(
        from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LightningBolt;setVisualOnly(Z)V"),
        to = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z")
    ))
    private int preventVanillaSnowAndIce(Random random, int bound, LevelChunk chunk, int randomTickSpeed)
    {
        // Targeting the random.nextInt(16) only
        return !TFCConfig.SERVER.enableVanillaWeatherEffects.get() && bound == 16 ? 1 : random.nextInt(bound);
    }

    @Inject(method = "tickChunk", at = @At(value = "TAIL"))
    private void onEnvironmentTick(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci)
    {
        if (!TFCConfig.SERVER.enableVanillaWeatherEffects.get())
        {
            final ServerLevel level = (ServerLevel) (Object) this;
            EnvironmentHelpers.tickChunk(level, chunk, level.getProfiler());
        }
    }
}

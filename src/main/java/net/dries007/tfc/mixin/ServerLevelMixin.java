/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.EnvironmentHelpers;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin
{
    /**
     * Don't do vanilla snow and ice generation if not enabled, as we have our own overrides
     */
    @Inject(method = "tickPrecipitation", at = @At("HEAD"), cancellable = true)
    private void preventVanillaSnowAndIce(BlockPos blockPos, CallbackInfo ci)
    {
        if (!TFCConfig.SERVER.enableVanillaWeatherEffects.getAsBoolean())
        {
            ci.cancel();
        }
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

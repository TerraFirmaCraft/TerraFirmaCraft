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

import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.tracker.WeatherHelpers;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin
{
    @Inject(method = "tickChunk", at = @At(value = "TAIL"))
    private void onEnvironmentTick(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci)
    {
        // Handle rain effects via the climate model
        WeatherHelpers.onTickChunk((ServerLevel) (Object) this, chunk);
    }

    @Inject(method = "tickPrecipitation", at = @At("HEAD"), cancellable = true)
    private void preventVanillaSnowAndIce(BlockPos blockPos, CallbackInfo ci)
    {
        // If we handle rain via the climate model, then prevent vanilla precipitation effects from occurring
        if (Climate.get((ServerLevel) (Object) this).supportsRain()) ci.cancel();
    }

    @Inject(method = "advanceWeatherCycle", at = @At("HEAD"), cancellable = true)
    private void doClimateBasedWeatherCycle(CallbackInfo ci)
    {
        // If we handle rain via the climate model, then prevent vanilla weather cycle handling
        if (WeatherHelpers.advanceWeatherCycle((ServerLevel) (Object) this)) ci.cancel();
    }
}

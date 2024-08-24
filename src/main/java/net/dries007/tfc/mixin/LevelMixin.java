/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.util.tracker.WeatherHelpers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin
{
    /**
     * Replace the default rainfall check, which by default only checks the biomes capability for rain, with one
     * that queries the climate model, if it supports that.
     */
    @WrapOperation(
        method = "isRainingAt",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private Biome.Precipitation isRainingAtWithClimate(Biome instance, BlockPos pos, Operation<Biome.Precipitation> original)
    {
        return WeatherHelpers.getPrecipitationAt((Level) (Object) this, pos, original.call(instance, pos));
    }


    /**
     * Replace the client side rain level query only, with one that is aware of the current player position,
     * and is able to linearly interpolate much better.
     */
    @Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true)
    private void getEnvironmentAdjustedRainLevelOnClient(float partialTick, CallbackInfoReturnable<Float> cir)
    {
        if (((Level) (Object) this).isClientSide())
        {
            cir.setReturnValue(ClimateRenderCache.INSTANCE.getRainLevel(partialTick));
        }
    }
}

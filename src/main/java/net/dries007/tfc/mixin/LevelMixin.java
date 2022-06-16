/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.climate.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin
{
    @Shadow
    public abstract boolean isClientSide();

    /**
     * The call to {@link Biome#getPrecipitation()} will always pass, as it's only checked against rain. We just need to check both climate and actual rainfall state here.
     */
    @Redirect(method = "isRainingAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean isRainingAtCheckClimateAndEnvironment(Biome biome, BlockPos pos)
    {
        final Level level = (Level) (Object) this;
        return Climate.warmEnoughToRain(level, pos) && EnvironmentHelpers.isRainingOrSnowing(level, pos);
    }

    /**
     * Replace the client side rain level query only, with one that is aware of the current player position, and is able to linearly interpolate much better.
     */
    @Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true)
    private void getEnvironmentAdjustedRainLevelOnClient(float partialTick, CallbackInfoReturnable<Float> cir)
    {
        if (isClientSide())
        {
            cir.setReturnValue(ClimateRenderCache.INSTANCE.getRainLevel(partialTick));
        }
    }
}

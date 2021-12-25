/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.util.climate.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Redirect two calls in {@link Level#isRainingAt(BlockPos)} to use climate.
 */
@Mixin(Level.class)
public abstract class LevelMixin
{
    @Redirect(method = "isRainingAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitation()Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    private Biome.Precipitation isRainingAtRedirectGetPrecipitation(Biome biome, BlockPos pos)
    {
        return Climate.getPrecipitation((Level) (Object) this, pos);
    }

    @Redirect(method = "isRainingAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean isRainingAtRedirectWarmEnoughToRain(Biome biome, BlockPos pos)
    {
        return Climate.warmEnoughToRain((Level) (Object) this, pos);
    }
}

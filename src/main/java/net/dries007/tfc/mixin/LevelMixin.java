/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.climate.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Level.class)
public abstract class LevelMixin
{
    /**
     * The call to {@link Biome#getPrecipitation()} will always pass, as it's only checked against rain. We just need to check both climate and actual rainfall state here.
     */
    @Redirect(method = "isRainingAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean isRainingAtCheckClimateAndEnvironment(Biome biome, BlockPos pos)
    {
        final Level level = (Level) (Object) this;
        return Climate.warmEnoughToRain(level, pos) && EnvironmentHelpers.isRainingOrSnowing(level, pos);
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.climate.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    @Shadow private ClientLevel level;

    /**
     * Redirect the call to {@link Biome#warmEnoughToRain(BlockPos)} with one that has a position and world context
     */

    @Redirect(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean renderSnowAndRainAddClimateCheck(Biome biome, BlockPos pos)
    {
        return Climate.warmEnoughToRain(level, pos);
    }

    /**
     * Redirect the call to {@link Biome#getPrecipitation()} to use {@link net.dries007.tfc.util.EnvironmentHelpers#isRainingOrSnowing(Level, BlockPos)}
     */
    @Redirect(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitation()Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    private Biome.Precipitation renderSnowAndRainAddEnvironmentHelpersCheck(Biome biome, LightTexture lightTexture, float partialTicks, double xIn, double yIn, double zIn)
    {
        return EnvironmentHelpers.isRainingOrSnowing(level, new BlockPos(xIn, yIn, zIn)) ? Biome.Precipitation.RAIN : Biome.Precipitation.NONE;
    }

    /**
     * Redirect the call to {@link Biome#warmEnoughToRain(BlockPos)}.
     * The {@link Biome#getPrecipitation()} is only checked against rain, and will always pass.
     * We just need to check both temperature / climate, and actual rainfall state here.
     */
    @Redirect(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean tickRainRedirectGetTemperature(Biome biome, BlockPos pos)
    {
        return Climate.warmEnoughToRain(level, pos) && EnvironmentHelpers.isRainingOrSnowing(level, pos);
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;

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
     * Redirect the call to {@link Biome#getTemperature(BlockPos)} with one that has a position and world context
     */
    @Redirect(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getTemperature(Lnet/minecraft/core/BlockPos;)F"))
    private float renderSnowAndRainRedirectGetTemperature(Biome biome, BlockPos pos)
    {
        return Climate.getVanillaBiomeTemperature(level, pos);
    }

    /**
     * Redirect the call to {@link Biome#getPrecipitation()} with one that has a position and world context
     */
    @Redirect(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitation()Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    private Biome.Precipitation renderSnowAndRainRedirectGetPrecipitation(Biome biome, LightTexture lightTexture, float partialTicks, double xIn, double yIn, double zIn)
    {
        return Climate.getPrecipitation(level, new BlockPos(xIn, yIn, zIn));
    }

    /**
     * Redirect the call to {@link Biome#getTemperature(BlockPos)} with one that has a position and world context
     */
    @Redirect(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getTemperature(Lnet/minecraft/core/BlockPos;)F"))
    private float tickRainRedirectGetTemperature(Biome biome, BlockPos pos)
    {
        return Climate.getVanillaBiomeTemperature(level, pos);
    }

    /**
     * Redirect the call to {@link Biome#getPrecipitation()} with one that has a position and world context
     */
    @Redirect(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitation()Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    private Biome.Precipitation tickRainRedirectGetPrecipitation(Biome biome, Camera activeRenderInfo)
    {
        return Climate.getPrecipitation(level, activeRenderInfo.getBlockPosition());
    }
}

/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.renderer;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.util.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin
{
    @Unique
    private final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
    @Shadow
    private ClientWorld level;

    /**
     * Redirect the call to {  Biome#getTemperature(BlockPos)} with one that has a position and world context
     */
    @Redirect(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    private float redirect$renderSnowAndRain$getTemperature(Biome biome, BlockPos pos)
    {
        return Climate.getVanillaBiomeTemperature(biome, level, pos);
    }

    /**
     * Redirect the call to {  Biome#getPrecipitation()} with one that has a position and world context
     */
    @Redirect(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;"))
    private Biome.RainType redirect$renderSnowAndRain$getPrecipitation(Biome biome, LightTexture lightmapIn, float partialTicks, double xIn, double yIn, double zIn)
    {
        mutablePos.setPos(xIn, yIn, zIn);
        return Climate.getVanillaBiomePrecipitation(biome, level, mutablePos);
    }

    /**
     * Redirect the call to {  Biome#getTemperature(BlockPos)} with one that has a position and world context
     */
    @Redirect(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    private float redirect$tickRain$getTemperature(Biome biome, BlockPos pos)
    {
        return Climate.getVanillaBiomeTemperature(biome, level, pos);
    }

    /**
     * Redirect the call to {  Biome#getPrecipitation()} with one that has a position and world context
     */
    @Redirect(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;"))
    private Biome.RainType redirect$tickRain$getPrecipitation(Biome biome, ActiveRenderInfo activeRenderInfo)
    {
        return Climate.getVanillaBiomePrecipitation(biome, level, activeRenderInfo.getBlockPos());
    }
}

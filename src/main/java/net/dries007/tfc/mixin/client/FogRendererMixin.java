/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;

import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.util.Helpers;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin
{
    /**
     * Replace the call to {@link Biome#getWaterFogColor()} with one that has a position context
     */
    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getWaterFogColor()I"))
    private static int setupColorRedirectGetWaterFogColor(Biome biome, Camera camera, float partialTicks, ClientLevel level)
    {
        return TFCColors.getWaterFogColor(level, biome, camera.getBlockPosition());
    }

    /**
     * Replace a call to {@link Biome#getFogColor()} with one that has a position context
     */
    @Dynamic("Lambda method in setupColor")
    @Redirect(method = "*(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/level/biome/BiomeManager;FIII)Lnet/minecraft/world/phys/Vec3;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getFogColor()I"))
    private static int setupColorRedirectGetFogColor(Biome biome, ClientLevel level, BiomeManager biomeManager, float brightness, int quartX, int quartY, int quartZ)
    {
        return TFCColors.getFogColor(level, biome, Helpers.quartToBlock(quartX, quartY, quartZ));
    }
}

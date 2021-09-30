/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.CubicSampler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.util.Helpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin
{
    @Unique
    private static final ThreadLocal<ClientLevel> setupColorClientLevel = ThreadLocal.withInitial(() -> null);

    /**
     * The later {@link ModifyArg} cannot capture target method arguments. We capture the level here
     */
    @Inject(method = "setupColor", at = @At(value = "HEAD"))
    private static void captureClientLevel(Camera camera, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci)
    {
        setupColorClientLevel.set(level);
    }

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
     * We use a modify arg here rather than redirecting the {@code getFogColor} call itself, as the inner lambda does not have access to the client level instance.
     */
    @ModifyArg(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;"))
    private static CubicSampler.Vec3Fetcher setupColorGetFogColorWithColormap(CubicSampler.Vec3Fetcher fetcher)
    {
        return (x, y, z) -> {
            final Level level = setupColorClientLevel.get();
            final Biome biome = level.getBiomeManager().getNoiseBiomeAtQuart(x, y, z);
            return Vec3.fromRGB24(TFCColors.getFogColor(level, biome, Helpers.quartToBlock(x, y, z)));
        };
    }
}

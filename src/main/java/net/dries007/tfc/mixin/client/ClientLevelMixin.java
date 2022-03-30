/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import java.util.function.Supplier;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.util.Helpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level
{
    protected ClientLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, Holder<DimensionType> dimensionType, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed)
    {
        super(levelData, dimension, dimensionType, profiler, isClientSide, isDebug, biomeZoomSeed);
    }

    /**
     * Replace a call to {@link Biome#getSkyColor()} with one that has a position and world context
     * We use a modify arg here rather than redirecting the {@code getSkyColor} call itself, as the inner lambda does not have access to the client level instance.
     */
    @ModifyArg(method = "getSkyColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;"))
    private CubicSampler.Vec3Fetcher getSkyColorWithColormap(CubicSampler.Vec3Fetcher fetcher)
    {
        return (x, y, z) -> {
            final Biome biome = getBiomeManager().getNoiseBiomeAtQuart(x, y, z).value();
            return Vec3.fromRGB24(TFCColors.getSkyColor(this, biome, Helpers.quartToBlock(x, y, z)));
        };
    }
}

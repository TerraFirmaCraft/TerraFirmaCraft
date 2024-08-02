/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.common.component.CachedMut;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.world.biome.BiomeBridge;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Biome.class)
public abstract class BiomeMixin implements BiomeBridge
{
    @Unique
    private final CachedMut<BiomeExtension> tfc$cachedExtension = CachedMut.unloaded();

    @Nullable
    @Override
    public BiomeExtension tfc$getExtension(@NotNull CommonLevelAccessor level)
    {
        if (!tfc$cachedExtension.isLoaded())
        {
            tfc$cachedExtension.load(TFCBiomes.findExtension(level, (Biome) (Object) this));
        }
        return tfc$cachedExtension.value();
    }

    /**
     * Use climate for weather, bypassing {@link Biome#warmEnoughToRain(BlockPos)}, and also use world tracker for additional rain check.
     */
    @Redirect(method = "shouldSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean betterShouldSnowCheck(Biome biome, BlockPos pos, LevelReader level)
    {
        return Climate.warmEnoughToRain(level, pos, biome);
    }

    /**
     * Redirect a call to {@link Biome#warmEnoughToRain(BlockPos)} with one that has a world and position context
     * <p>
     * In vanilla this is either called from ServerWorld, or from world generation with ISeedReader - both of which are able to cast up to IWorld.
     * FFor cases where this cast is not valid we just default to the vanilla temperature.
     */
    @Redirect(method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean shouldFreezeWithClimate(Biome biome, BlockPos pos, LevelReader level)
    {
        return Climate.warmEnoughToRain(level, pos, biome);
    }
}

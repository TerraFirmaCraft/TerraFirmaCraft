/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.dries007.tfc.client.overworld.SolarCalculator;
import net.dries007.tfc.util.tracker.WeatherHelpers;

@Mixin(Monster.class)
public abstract class MonsterMixin
{
    @WrapOperation(method = "isDarkEnoughToSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerLevelAccessor;getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;)I"))
    private static int isDarkEnoughToSpawnWithSolarSkyDarken(ServerLevelAccessor level, BlockPos pos, Operation<Integer> original)
    {
        return level.getMaxLocalRawBrightness(pos, SolarCalculator.getSkyDarken(level.getLevel(), pos));
    }

    @WrapOperation(method = "isDarkEnoughToSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isThundering()Z"))
    private static boolean isDarkEnoughToSpawnWithLocalThunder(ServerLevel level, Operation<Boolean> original, ServerLevelAccessor levelAccessor, BlockPos pos, RandomSource random)
    {
        return original.call(level) && WeatherHelpers.getPrecipitationAt(level, pos, Biome.Precipitation.RAIN) != Biome.Precipitation.NONE;
    }
}

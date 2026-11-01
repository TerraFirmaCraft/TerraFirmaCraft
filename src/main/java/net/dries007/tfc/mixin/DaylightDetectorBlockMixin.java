/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.dries007.tfc.client.overworld.SolarCalculator;

@Mixin(DaylightDetectorBlock.class)
public abstract class DaylightDetectorBlockMixin
{
    @WrapOperation(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getSkyDarken()I"))
    private static int updateSignalStrengthWithSolarSkyDarken(Level level, Operation<Integer> original, BlockState state, Level levelArg, BlockPos pos)
    {
        return SolarCalculator.getSkyDarken(level, pos);
    }

    @WrapOperation(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getSunAngle(F)F"))
    private static float updateSignalStrengthWithSolarSunAngle(Level level, float partialTick, Operation<Float> original, BlockState state, Level levelArg, BlockPos pos)
    {
        return SolarCalculator.getSunAngle(level, pos);
    }
}

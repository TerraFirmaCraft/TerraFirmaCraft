/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.MixingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This modifies vanilla fluid behavior (enabled via the "mixing" fluid tag), in order to play nicely with {@link MixingFluid}.
 */
@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin extends Fluid
{

    @Shadow
    protected abstract int getDropOff(LevelReader worldIn);

    @Inject(method = "getNewLiquid", at = @At("HEAD"), cancellable = true)
    private void getNewLiquidWithMixing(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<FluidState> cir)
    {
        if (FluidHelpers.canMixFluids(this))
        {
            cir.setReturnValue(FluidHelpers.getNewFluidWithMixing((FlowingFluid) (Object) this, level, pos, state, getDropOff(level)));
        }
    }
}

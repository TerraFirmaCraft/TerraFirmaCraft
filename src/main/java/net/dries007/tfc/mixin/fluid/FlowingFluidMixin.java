/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import net.dries007.tfc.common.fluids.FluidHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This modifies vanilla fluid behavior (enabled via the "mixing" fluid tag), in order to play nicely with {  MixingFluid}.
 */
@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin extends Fluid
{
    @Shadow
    protected abstract boolean canSourcesMultiply();

    @Shadow
    protected abstract int getLevelDecreasePerBlock(IWorldReader worldIn);

    @Inject(method = "getNewLiquid", at = @At("HEAD"), cancellable = true)
    private void inject$getNewLiquid(IWorldReader worldIn, BlockPos pos, BlockState blockStateIn, CallbackInfoReturnable<FluidState> cir)
    {
        if (FluidHelpers.canMixFluids(this))
        {
            cir.setReturnValue(FluidHelpers.getNewFluidWithMixing((FlowingFluid) (Object) this, worldIn, pos, blockStateIn, canSourcesMultiply(), getLevelDecreasePerBlock(worldIn)));
        }
    }
}

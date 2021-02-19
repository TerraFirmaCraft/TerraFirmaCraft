/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.renderer;

import net.minecraft.client.renderer.FluidBlockRenderer;
import net.minecraft.fluid.Fluid;

import net.dries007.tfc.common.fluids.FluidHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FluidBlockRenderer.class)
public abstract class FluidBlockRendererMixin
{
    /**
     * This is used to determine if two fluids are the same, and thus should not render the face between them
     * We redirect the sameness call to one which compares for mixable fluids as well
     * Non-critical, so require none.
     */
    @Redirect(method = "isNeighborSameFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/Fluid;isEquivalentTo(Lnet/minecraft/fluid/Fluid;)Z"), require = 0)
    private static boolean redirect$isNeighborSameFluid$isEquivalentTo(Fluid fluid, Fluid fluidIn)
    {
        return fluid.isEquivalentTo(fluidIn) || FluidHelpers.canMixFluids(fluid, fluidIn);
    }

    /**
     * This is used to determine fluid block height.
     * We redirect the sameness call to one which compares for mixable fluids as well.
     * Non-critical, so require none.
     */
    @Redirect(method = "getWaterHeight", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/Fluid;isEquivalentTo(Lnet/minecraft/fluid/Fluid;)Z"), require = 0)
    private boolean redirect$getWaterHeight$isEquivalentTo(Fluid fluid, Fluid fluidIn)
    {
        return fluid.isEquivalentTo(fluidIn) || FluidHelpers.canMixFluids(fluid, fluidIn);
    }
}
